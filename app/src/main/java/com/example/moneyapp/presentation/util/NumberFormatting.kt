package com.example.moneyapp.presentation.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

fun BigDecimal.formattedRate(
    locale: Locale = Locale.getDefault()
): String =
    NumberFormat.getNumberInstance(locale).apply {
        maximumFractionDigits = 6
    }.format(this)


fun BigDecimal.formattedChange(
    locale: Locale = Locale.getDefault()
): String {
    val formatted = NumberFormat.getPercentInstance(locale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        roundingMode = RoundingMode.HALF_EVEN
    }.format(this)
    return if (signum() > 0) "+$formatted" else formatted
}