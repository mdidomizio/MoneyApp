package com.example.moneyapp.di

import com.example.moneyapp.data.AndroidConnectivityObserver
import com.example.moneyapp.data.RateRepositoryImpl
import com.example.moneyapp.domain.ConnectivityObserver
import com.example.moneyapp.domain.RatesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRateRepository(
        impl: RateRepositoryImpl
    ): RatesRepository

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(
        impl: AndroidConnectivityObserver
    ): ConnectivityObserver
}