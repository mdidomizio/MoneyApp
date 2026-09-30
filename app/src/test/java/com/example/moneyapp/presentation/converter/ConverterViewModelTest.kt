package com.example.moneyapp.presentation.converter

import com.example.moneyapp.MainDispatcherRule
import com.example.moneyapp.domain.model.Money
import com.example.moneyapp.fake.FakeRatesRepository
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class ConverterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `converts the entered amount with the loaded rate`() {
        val viewModel = ConverterViewModel(FakeRatesRepository())
        viewModel.onAction(ConverterAction.OnAmountChanged("100"))

        val converted = viewModel.uiState.value.converted?.rounded()

        assertEquals(
            Money(BigDecimal("115.68"),
                "USD"),
            converted
        )
    }

    @Test
    fun `swap requests the reversed pair`() {
        val repository = FakeRatesRepository()
        val viewModel = ConverterViewModel(repository)

        viewModel.onAction(ConverterAction.OnSwapClicked)

        assertEquals(
            "USD" to "EUR",
            repository.requestedPairs.last())
    }
}