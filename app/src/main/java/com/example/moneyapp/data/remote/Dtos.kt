package com.example.moneyapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class RateDto(
    val date: String,
    val base: String,
    val quote: String,
    @Serializable(with = BigDecimalSerializer::class)
    val rate: BigDecimal,
)

@Serializable
data class CurrencyDto(
    @SerialName("iso_code") val isoCode: String,
    val name: String,
    val symbol: String? = null,
)
