package com.example.moneyapp.presentation.util


import com.example.moneyapp.domain.model.Money
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


fun Money.formatted(locale: Locale = Locale.getDefault()): String {
    val value = rounded()
    val javaCurrency =
        runCatching { Currency.getInstance(currency) }.getOrNull()
            ?:return "${NumberFormat
                .getCurrencyInstance(locale)
                .format(value.amount)} $currency"

    return NumberFormat.getCurrencyInstance(locale).apply {
        this.currency = javaCurrency
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }.format(value.amount)

}