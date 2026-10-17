package com.example.moneyapp.di

import android.content.Context
import androidx.room.Room
import com.example.moneyapp.data.local.CurrencyDao
import com.example.moneyapp.data.local.MoneyDataBase
import com.example.moneyapp.data.local.RateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoneyDataBase =
        Room
            .databaseBuilder(
                context,
                MoneyDataBase::class.java,
                "money.db"
            )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideRateDao(dataBase: MoneyDataBase): RateDao =
        dataBase.rateDao()

    @Provides
    fun provideCurrencyDao(dataBase: MoneyDataBase): CurrencyDao =
        dataBase.currencyDao()

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemUTC()
}