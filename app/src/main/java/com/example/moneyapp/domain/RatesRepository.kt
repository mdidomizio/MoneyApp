package com.example.moneyapp.domain

import com.example.moneyapp.domain.model.Currency
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.util.DataError
import com.example.moneyapp.domain.util.Result
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.ContentConvertException
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.SerializationException
import okio.IOException
import java.time.LocalDate
import java.time.format.DateTimeParseException

interface RatesRepository {
    suspend fun getCurrencies(): Result<List<Currency>, DataError>
    suspend fun getLatestRate(
        base: String,
        quote: String
    ): Result<ExchangeRate, DataError>

    suspend fun getRateHistory(
        base: String,
        quote: String,
        from: LocalDate
    ): Result<List<ExchangeRate>, DataError>
}