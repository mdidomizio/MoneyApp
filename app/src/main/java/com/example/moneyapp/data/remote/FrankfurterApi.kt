package com.example.moneyapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.LocalDate
import javax.inject.Inject

class FrankfurterApi @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun getCurrencies(): List<CurrencyDto> =
        client.get("currencies").body()

    suspend fun getRate(
        base: String,
        quote: String
    ): RateDto =
        client.get("rate/$base/$quote").body()

    suspend fun getRateHistory(
        base: String,
        quote: String,
        from: LocalDate
    ): List<RateDto> =
        client.get("rates"){
            parameter("base", base)
            parameter("quotes", quote)
            parameter("from", from.toString())
        }.body()
}