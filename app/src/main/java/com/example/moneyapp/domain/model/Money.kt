package com.example.moneyapp.domain.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

data class Money(
    val amount: BigDecimal,
    val currency: String
) {
    val fractionDigits: Int
        get() = runCatching {
            Currency
                .getInstance(currency)
                .defaultFractionDigits
        }
            .getOrNull()
            ?.takeIf { it >= 0 }
            ?: 2

    fun convertWith(rate: ExchangeRate): Money {
        require( rate.base == currency) {
            "Rate is for ${rate.base}, but this amount is in $currency"
        }
        return Money(
            amount = amount * rate.rate,
            currency = rate.quote
        )
    }

    fun rounded(): Money = copy(
        amount = amount.setScale(fractionDigits, RoundingMode.HALF_EVEN)
    )
}
