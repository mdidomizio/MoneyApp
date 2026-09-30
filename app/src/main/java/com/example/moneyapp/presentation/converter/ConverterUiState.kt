package com.example.moneyapp.presentation.converter

import com.example.moneyapp.domain.model.Currency
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.model.Money
import com.example.moneyapp.domain.util.DataError
import java.math.BigDecimal

data class ConverterUiState(
    val currencies: List<Currency> = emptyList(),
    val amountInput: String = "",
    val from: String = "EUR",
    val to: String = "USD",
    val rate: ExchangeRate? = null,
    val history: List<ExchangeRate> = emptyList(),
    val isLoading: Boolean = false,
    val error: DataError? = null
) {
    val amount: BigDecimal?
        get() = amountInput.replace(',', '.').toBigDecimalOrNull()

    val converted: Money?
        get() {
            val currentRate =
                rate?.takeIf {
                    it.base == from && it.quote == to
                } ?: return null
            val value = amount ?: return null

            return Money(value, from).convertWith(currentRate)
        }
}