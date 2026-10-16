package com.example.moneyapp.domain.model

import java.time.Instant

sealed interface RateSource {
    data object Network : RateSource
    data class Cache(
        val savedAt: Instant
    ) : RateSource
}