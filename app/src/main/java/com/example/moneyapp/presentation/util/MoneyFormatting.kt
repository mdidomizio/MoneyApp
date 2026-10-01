package com.example.moneyapp.presentation.util


import com.example.moneyapp.domain.model.Money
import java.text.NumberFormat
import java.util.Locale


fun Money.formatted(locale: Locale = Locale.getDefault()): String {
    val number = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }.format(rounded().amount)

    return "$number $currency"
}