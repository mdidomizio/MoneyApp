package com.example.moneyapp.presentation.converter

import com.example.moneyapp.MainDispatcherRule
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.model.LatestRate
import com.example.moneyapp.domain.model.Money
import com.example.moneyapp.domain.model.RateSource
import com.example.moneyapp.domain.util.DataError
import com.example.moneyapp.domain.util.Result
import com.example.moneyapp.fake.FakeConnectivityObserver
import com.example.moneyapp.fake.FakeRatesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class ConverterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `converts the entered amount with the loaded rate`() {
        val viewModel = ConverterViewModel(  FakeRatesRepository(), FakeConnectivityObserver())
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
        val connectivityObserver = FakeConnectivityObserver()
        val viewModel = ConverterViewModel(repository, connectivityObserver)

        viewModel.onAction(ConverterAction.OnSwapClicked)

        assertEquals(
            "USD" to "EUR",
            repository.requestedPairs.last())
    }

    @Test
    fun `a cached rate exposes when it was saved`(): Unit {
        val savedAt = Instant.parse("2026-10-21T09:22:00Z")
        val repository = FakeRatesRepository().apply {
            rateResult = Result.Success(
                LatestRate(
                    rate = ExchangeRate(
                        "EUR",
                        "USD",
                        BigDecimal("1.1568"),
                        LocalDate.of(2026, 10, 21)
                    ),
                    source = RateSource.Cache(savedAt),
                )
            )
        }
        val viewModel = ConverterViewModel(repository, FakeConnectivityObserver())

        val rateState = viewModel.uiState.value.rateState as RateUiState.Success
        assertEquals(savedAt, rateState.savedAt)
    }

    @Test
    fun `a fresh rate has no saved time`(): Unit {
        val savedAt = null
        val repository = FakeRatesRepository().apply {
            rateResult = Result.Success(
                LatestRate(
                    rate = ExchangeRate(
                        "EUR",
                        "USD",
                        BigDecimal("1.1568"),
                        LocalDate.of(2026, 10, 21)
                    ),
                    source = RateSource.Network,
                )
            )
        }
        val viewModel = ConverterViewModel(repository, FakeConnectivityObserver())

        val rateState = viewModel.uiState.value.rateState as RateUiState.Success
        assertNull(rateState.savedAt)
    }
}