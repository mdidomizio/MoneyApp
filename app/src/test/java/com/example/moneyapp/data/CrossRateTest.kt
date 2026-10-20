package com.example.moneyapp.data

import com.example.moneyapp.data.local.CachedRateEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class CrossRatesTest {

    private fun eur(quote: String, rate: String, date: String = "2026-10-20") =
        CachedRateEntity(base = "EUR", quote = quote, rate = rate, date = date, fetchedAt = 0L)

    @Test
    fun `derives USD to GBP from the two EUR rates`() {
        val result = deriveCrossRate("USD", "GBP", eur("USD", "1.1226"), eur("GBP", "0.84187"))

        assertEquals(
            BigDecimal("0.7499"),
            result?.rate?.rate?.setScale(4, RoundingMode.HALF_EVEN),
        )
    }

    @Test
    fun `uses the older date of the two legs`() {
        val result = deriveCrossRate(
            "GBP", "XOF",
            eur("GBP", "0.84187", date = "2026-10-20"),
            eur("XOF", "655.49", date = "2026-10-19"),
        )

        assertEquals(LocalDate.of(2026, 10, 19), result?.rate?.date)
    }

    @Test
    fun `returns null when a leg is missing`() {
        assertNull(deriveCrossRate("USD", "GBP", eur("USD", "1.1226"), null))
    }

    @Test
    fun `returns null when a leg is too imprecise`() {
        assertNull(deriveCrossRate("USD", "XAU", eur("USD", "1.1226"), eur("XAU", "0.00027")))
    }

    @Test
    fun `a pair from EUR uses the single leg directly`() {
        val result = deriveCrossRate("EUR", "GBP", null, eur("GBP", "0.84187"))

        assertEquals(0, BigDecimal("0.84187").compareTo(result?.rate?.rate))
    }
}