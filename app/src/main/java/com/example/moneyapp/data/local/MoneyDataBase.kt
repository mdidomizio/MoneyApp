package com.example.moneyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CachedRateEntity::class,
        CachedCurrencyEntity::class
               ],
    version = 2,
    exportSchema = false
)
abstract class MoneyDataBase : RoomDatabase() {
    abstract fun rateDao(): RateDao
    abstract fun currencyDao(): CurrencyDao
}