package com.example.moneyapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.moneyapp.domain.model.Currency

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM cached_currencies ORDER BY code")
    suspend fun getAll(): List<CachedCurrencyEntity>

    @Insert
    suspend fun insertAll(currencies: List<CachedCurrencyEntity>)

    @Query("DELETE FROM cached_currencies")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(currencies: List<CachedCurrencyEntity>) {
        deleteAll()
        insertAll(currencies)
    }
}