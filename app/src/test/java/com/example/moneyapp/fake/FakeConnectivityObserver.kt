package com.example.moneyapp.fake

import com.example.moneyapp.domain.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeConnectivityObserver : ConnectivityObserver {
    val onLine = MutableStateFlow(true)
    override val isOnLine: Flow<Boolean> = onLine
}