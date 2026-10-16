package com.example.moneyapp.domain.model

data class LatestRate(
    val rate: ExchangeRate,
    val source: RateSource
)
