package com.example.moneyapp.data.local

import androidx.room.Entity

@Entity(tableName = "cached_rates", primaryKeys = ["base", "quote"])
data class CachedRateEntity(
    val base: String,
    val quote: String,
    val rate: String,
    val date: String,
    val fetchedAt: Long
)
