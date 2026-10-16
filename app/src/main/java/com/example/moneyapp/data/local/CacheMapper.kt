package com.example.moneyapp.data.local

import com.example.moneyapp.domain.model.ExchangeRate
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

fun ExchangeRate.toEntity(fetchedAt: Instant) = CachedRateEntity(
    base = base,
    quote = quote,
    rate = rate.toPlainString(),
    date = date.toString(),
    fetchedAt = fetchedAt.toEpochMilli(),
)

fun CachedRateEntity.toDomain() = ExchangeRate(
    base = base,
    quote = quote,
    rate = BigDecimal(rate),
    date = LocalDate.parse(date)
)