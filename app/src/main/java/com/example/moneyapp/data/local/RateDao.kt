package com.example.moneyapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RateDao {
    @Upsert
    suspend fun upsert(rate: CachedRateEntity)

    @Upsert
    suspend fun upsertAll(rates: List<CachedRateEntity>)

    @Query("SELECT * FROM cached_rates WHERE base = :base AND quote = :quote")
    suspend fun get(base: String, quote: String): CachedRateEntity?
}