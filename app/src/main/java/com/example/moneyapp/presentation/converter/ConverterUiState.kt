package com.example.moneyapp.presentation.converter

import com.example.moneyapp.domain.model.Currency
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.model.Money
import com.example.moneyapp.domain.util.DataError
import java.math.BigDecimal

sealed interface RateUiState {
    data object Loading : RateUiState
    data class Error(
        val errorMessage: DataError
    ) : RateUiState

    data class Success(
        val rate: ExchangeRate,
        val history: List<ExchangeRate>
    ) : RateUiState
}

data class ConverterUiState(
    val currencies: List<Currency> = emptyList(),
    val amountInput: String = "",
    val from: String = "EUR",
    val to: String = "USD",
    val rateState: RateUiState = RateUiState.Loading
) {
    /// BigDecimal uses commas, but for convention we want to use dots
    val amount: BigDecimal?
        get() = amountInput.replace(',', '.').toBigDecimalOrNull()

    val converted: Money?
        get() {
            val success = rateState as? RateUiState.Success ?: return null

            val rate = success.rate
                .takeIf {
                    it.base == from
                            && it.quote == to
                }
                ?: return null
            val value = amount ?: return null


            return Money(value, from).convertWith(rate)
        }
}