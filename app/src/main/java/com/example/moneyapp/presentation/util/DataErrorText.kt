package com.example.moneyapp.presentation.util

import com.example.moneyapp.domain.util.DataError

fun DataError.toMessage(): String = when (this) {
    DataError.NO_INTERNET -> "No connection. Check your internet and try again."
    DataError.NOT_FOUND -> "No rate available for this currency pair. Try another currency."
    DataError.INVALID_REQUEST -> "This currency pair isn't supported. Try another currency."
    DataError.SERVER -> "The rates service isn't responding. Try again in a moment."
    DataError.SERIALIZATION -> "The app couldn't read the rate data."
    DataError.UNKNOWN -> "Something went wrong. Try again."
}