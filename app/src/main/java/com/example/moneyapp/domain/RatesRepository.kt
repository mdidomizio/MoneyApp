package com.example.moneyapp.domain

import com.example.moneyapp.domain.model.Currency
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.util.DataError
import com.example.moneyapp.domain.util.Result
import java.time.LocalDate

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