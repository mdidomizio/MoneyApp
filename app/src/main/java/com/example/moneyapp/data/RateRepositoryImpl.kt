package com.example.moneyapp.data

import android.util.Log
import com.example.moneyapp.data.remote.FrankfurterApi
import com.example.moneyapp.data.remote.toDomain
import com.example.moneyapp.domain.RatesRepository
import com.example.moneyapp.domain.model.Currency
import com.example.moneyapp.domain.model.ExchangeRate
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
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

class RateRepositoryImpl @Inject constructor(
    private val api: FrankfurterApi
) : RatesRepository {
    override suspend fun getCurrencies(): Result<List<Currency>, DataError> =
        safeCall {
            api.getCurrencies()
                .map { it.toDomain() }
                .sortedBy { it.code }
        }

    override suspend fun getLatestRate(
        base: String,
        quote: String
    ): Result<ExchangeRate, DataError> =
        safeCall { api.getRate(base, quote).toDomain() }

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
        } catch (e: CancellationException){
            throw  e
        } catch (e: ClientRequestException) {
            Result.Error(
                if (e.response.status.value == 404) DataError.NOT_FOUND else DataError.INVALID_REQUEST
            )
        } catch (e: ServerResponseException) {
            Result.Error(DataError.SERVER)
        } catch (e: SocketTimeoutException) {  //java.net
            Result.Error(DataError.TIMEOUT)
        }  catch (e: HttpRequestTimeoutException) { //io.ktorclient.plugins
            Result.Error(DataError.SERVER)
        }  catch (e: IOException){
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
}