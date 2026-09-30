package com.example.moneyapp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class ExchangeRate(
    val base: String,
    val quote: String,
    val rate: BigDecimal,
    val date: LocalDate
)
