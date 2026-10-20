package com.example.moneyapp.data

import com.example.moneyapp.data.local.CachedRateEntity
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.model.LatestRate
import com.example.moneyapp.domain.model.RateSource
import io.ktor.http.cio.CIOMultipartDataBase
import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant
import java.time.LocalDate

internal const val PIVOT = "EUR"
private const val   MIN_PRECISION = 3

/**
 * Derives base→quote from two cached EUR rates: (EUR→quote) / (EUR→base).
 * A null leg means that side is EUR itself. Returns null if a needed leg
 * is missing or too imprecise to calculate with.
 */

internal fun deriveCrossRate(
    base: String,
    quote: String,
    pivotToBase: CachedRateEntity?,
    pivotToQuote: CachedRateEntity?
): LatestRate? {
    if (base != PIVOT && pivotToBase == null) return null
    if (quote != PIVOT && pivotToQuote == null) return null

    val legs = listOfNotNull(pivotToBase, pivotToQuote)
    if (legs.isEmpty()) return null
    if (legs.any { BigDecimal(it.rate).precision() < MIN_PRECISION }) return null

    val baseRate = pivotToBase?.let { BigDecimal(it.rate) } ?: BigDecimal.ONE
    val quoteRate = pivotToQuote?.let { BigDecimal(it.rate) } ?: BigDecimal.ONE

    return LatestRate(
        rate = ExchangeRate(
            base = base,
            quote = quote,
            rate = quoteRate.divide(baseRate, MathContext.DECIMAL64),
            date = legs.minOf{ LocalDate.parse(it.date) },
        ),
        source = RateSource.Cache(
            savedAt = Instant.ofEpochMilli(
                legs.minOf{ it.fetchedAt }
            )
        ),
    )
}