package com.example.moneyapp.domain

import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.domain.model.Money
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class MoneyTest {

    private val eurUsd =
        ExchangeRate(
            "EUR",
            "USD",
            BigDecimal("1.1568"),
            LocalDate.of(2026, 9, 29)
        )

    @Test
    fun `converts exactly with BigDecimal`() {
        val result = Money(
                BigDecimal("100"),
                "EUR"
            )
                .convertWith(eurUsd)
                .rounded()

        assertEquals(
            Money(
                BigDecimal("115.68"),
                "USD"
            ),
            result
        )
    }

    @Test
    fun `JPY rounds to zero decimals using banker's rounding`() {
        assertEquals(
            BigDecimal("1234"),
            Money(
                BigDecimal("1234.5"),
                "JPY"
            )
                .rounded()
                .amount
        )

        assertEquals(
            BigDecimal("1236"),
            Money(
                BigDecimal("1235.5"),
                "JPY"
            )
                .rounded()
                .amount)
    }

    @Test
    fun `BHD keeps three decimals`() {
        assertEquals(BigDecimal("1.235"), Money(BigDecimal("1.2346"), "BHD").rounded().amount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects a rate for the wrong base currency`() {
        Money(BigDecimal("100"), "GBP").convertWith(eurUsd)
    }
}