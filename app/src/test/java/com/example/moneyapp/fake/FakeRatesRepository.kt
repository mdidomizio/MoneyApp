package com.example.moneyapp.fake

import com.example.moneyapp.domain.RatesRepository
import com.example.moneyapp.domain.model.Currency
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.util.DataError
import com.example.moneyapp.domain.util.Result
import java.math.BigDecimal
import java.time.LocalDate

class FakeRatesRepository : RatesRepository {

    var rateResult: Result<ExchangeRate, DataError> =
        Result.Success(ExchangeRate("EUR", "USD", BigDecimal("1.1568"), LocalDate.of(2026, 9, 29)))

    val requestedPairs = mutableListOf<Pair<String, String>>()

    override suspend fun getCurrencies(): Result<List<Currency>, DataError> =
        Result.Success(emptyList())

    override suspend fun getLatestRate(base: String, quote: String): Result<ExchangeRate, DataError> {
        requestedPairs += base to quote
        return rateResult
    }

    override suspend fun getRateHistory(
        base: String,
        quote: String,
        from: LocalDate,
    ): Result<List<ExchangeRate>, DataError> = Result.Success(emptyList())
}