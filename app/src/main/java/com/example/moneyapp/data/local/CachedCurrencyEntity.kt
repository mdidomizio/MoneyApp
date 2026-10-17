package com.example.moneyapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_currencies")
data class CachedCurrencyEntity(
    @PrimaryKey val code: String,
    val name: String,
    val symbol: String?,
)
