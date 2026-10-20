package com.example.moneyapp.domain

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isOnLine: Flow<Boolean>
}