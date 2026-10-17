package com.example.moneyapp.data

import com.example.moneyapp.data.local.CurrencyDao
import com.example.moneyapp.data.local.RateDao
import com.example.moneyapp.data.local.toDomain
import com.example.moneyapp.data.local.toEntity
import com.example.moneyapp.data.remote.FrankfurterApi
import com.example.moneyapp.data.remote.toDomain
import com.example.moneyapp.domain.RatesRepository
import com.example.moneyapp.domain.model.Currency
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.model.LatestRate
import com.example.moneyapp.domain.model.RateSource
import com.example.moneyapp.domain.util.DataError
import com.example.moneyapp.domain.util.Result
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.ContentConvertException
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.SerializationException
import okio.IOException
import java.net.SocketTimeoutException
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

class RateRepositoryImpl @Inject constructor(
    private val api: FrankfurterApi,
    private val rateDao: RateDao,
    private val currencyDao: CurrencyDao,
    private val clock: Clock
) : RatesRepository {
    override suspend fun getCurrencies(): Result<List<Currency>, DataError> {
        val remote = safeCall {
            api.getCurrencies()
                .map { it.toDomain() }
                .sortedBy { it.code }
        }

        return when (remote) {
            is Result.Success -> {
                currencyDao.replaceAll(
                    remote.data.map { it.toEntity() }
                )
                remote
            }

            is Result.Error -> {
                val cached =
                    if (remote.error in FALLBACK_ERRORS) currencyDao.getAll()
                    else emptyList()

                if (cached.isNotEmpty()) Result.Success(cached.map { it.toDomain() })
                else remote
            }
        }
    }


    override suspend fun getLatestRate(
        base: String,
        quote: String
    ): Result<LatestRate, DataError> {
        val remote = safeCall { api.getRate(base, quote).toDomain() }

        return when (remote) {
            is Result.Success -> {
                rateDao.upsert(remote.data.toEntity(fetchedAt = clock.instant()))
                Result.Success(LatestRate(remote.data, RateSource.Network))
            }

            is Result.Error -> {
                val cached =
                    if (remote.error in FALLBACK_ERRORS) rateDao.get(base, quote)
                    else null

                if (cached != null) {
                    Result.Success(
                        LatestRate(
                            rate = cached.toDomain(),
                            source = RateSource.Cache(
                                savedAt = Instant.ofEpochMilli(
                                    cached.fetchedAt
                                )
                            ),
                        )
                    )
                } else remote
            }
        }
    }


    override suspend fun getRateHistory(
        base: String,
        quote: String,
        from: LocalDate
    ): Result<List<ExchangeRate>, DataError> =
        safeCall {
            api.getRateHistory(base, quote, from)
                .filter { it.quote == quote }
                .map { it.toDomain() }
                .sortedBy { it.date }
        }

    private suspend fun <T> safeCall(
        block: suspend () -> T
    ): Result<T, DataError> =
        try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            Result.Error(
                if (e.response.status.value == 404) DataError.NOT_FOUND else DataError.INVALID_REQUEST
            )
        } catch (e: ServerResponseException) {
            Result.Error(DataError.SERVER)
        } catch (e: SocketTimeoutException) {  //java.net
            Result.Error(DataError.TIMEOUT)
        } catch (e: HttpRequestTimeoutException) { //io.ktorclient.plugins
            Result.Error(DataError.SERVER)
        } catch (e: IOException) {
            Result.Error(DataError.NO_INTERNET)
        } catch (e: SerializationException) {
            Result.Error(DataError.SERIALIZATION)
        } catch (e: ContentConvertException) {
            Result.Error(DataError.SERIALIZATION)
        } catch (e: DateTimeParseException) {
            Result.Error(DataError.SERIALIZATION)
        } catch (e: Exception) {
            Result.Error(DataError.UNKNOWN)
        }

    private companion object {
        val FALLBACK_ERRORS =
            setOf(
                DataError.NO_INTERNET,
                DataError.SERVER,
                DataError.TIMEOUT
            )
    }
}
