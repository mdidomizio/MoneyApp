package com.example.moneyapp.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyapp.domain.RatesRepository
import com.example.moneyapp.domain.util.Result
import com.example.moneyapp.presentation.converter.RateUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: RatesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState = _uiState.asStateFlow()

    private var pairJob: Job? = null

    init {
        loadCurrencies()
        loadPair()
    }

    private fun loadPair() {
        pairJob?.cancel()
        val from = uiState.value.from
        val to = uiState.value.to

        pairJob = viewModelScope.launch {
            _uiState.update {
                it.copy(rateState = Loading)
            }

            val rate = async { repository.getLatestRate(from, to) }
            val history = async {
                repository.getRateHistory(
                    from,
                    to,
                    LocalDate.now()
                        .minusDays(30)
                )
            }
            val rateResult = rate.await()
            val historyResult = history.await()

            val newRateState = when (rateResult) {
                is Result.Success -> Success(
                    rate = rateResult.data.rate,
                    history = (historyResult as? Result.Success)?.data.orEmpty()
                )

                is Result.Error -> Error(rateResult.error)
            }

            _uiState.update {
                it.copy( rateState = newRateState)
            }
        }
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            when (val result = repository.getCurrencies()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            currencies = result.data
                        )
                    }
                }

                is Result.Error -> Unit
            }
        }
    }

    private fun selectPair(
        from: String = uiState.value.from,
        to: String = uiState.value.to
    ) {
        _uiState.update {
            it.copy(
                from = from,
                to = to
            )
        }
        loadPair()
    }

    private fun updateAmount(input: String) {
        val sanitized = input.filter {
            it.isDigit() || it == '.' || it ==','
        }
        if (sanitized.count{ it == '.' || it == ','} > 1) return
        _uiState.update {
            it.copy(
                amountInput = sanitized
            )
        }
    }

    private fun swap() = selectPair(from = uiState.value.to, to = uiState.value.from)

    fun onAction(action: ConverterAction) {
        when (action) {
            is ConverterAction.FromSelectedCode -> {
                if (action.code == uiState.value.to) swap()
                else selectPair(from = action.code)
            }

            is ConverterAction.OnAmountChanged -> updateAmount(action.value)

            ConverterAction.OnRetryClicked -> {
                if (uiState.value.currencies.isEmpty()) loadCurrencies()
                loadPair()
            }

            ConverterAction.OnSwapClicked -> swap()

            is ConverterAction.ToSelectedCode -> {
                if (action.code == uiState.value.from) swap()
                else selectPair(to = action.code)
            }
        }
    }
}