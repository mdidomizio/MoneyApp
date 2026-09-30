package com.example.moneyapp.domain.util

sealed interface Result<out D, out E> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E>(val error: E) : Result<Nothing, E>
}

enum class DataError {
    NO_INTERNET,
    NOT_FOUND,
    INVALID_REQUEST,
    SERVER,
    SERIALIZATION,
    UNKNOWN
}