package com.example.moneyapp.data.remote

import com.example.moneyapp.domain.model.Currency
import com.example.moneyapp.domain.model.ExchangeRate
import java.time.LocalDate

fun CurrencyDto.toDomain(): Currency {
    return Currency(
        code = isoCode,
        name = name,
        symbol = symbol
    )
}

fun RateDto.toDomain(): ExchangeRate {
    return ExchangeRate(
        base = base,
        quote = quote,
        rate = rate,
        date = LocalDate.parse(date)
    )
}