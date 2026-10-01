package com.example.moneyapp.presentation.util

import androidx.annotation.StringRes
import com.example.moneyapp.R
import com.example.moneyapp.domain.util.DataError

@StringRes
fun DataError.toMessageRes(): Int = when (this) {
    DataError.NO_INTERNET -> R.string.error_no_internet
    DataError.TIMEOUT -> R.string.error_timeout
    DataError.NOT_FOUND -> R.string.error_not_found
    DataError.INVALID_REQUEST -> R.string.error_invalid_request
    DataError.SERVER -> R.string.error_server
    DataError.SERIALIZATION -> R.string.error_serialization
    DataError.UNKNOWN -> R.string.error_unknown
}