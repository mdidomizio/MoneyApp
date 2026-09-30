package com.example.moneyapp.presentation.converter

sealed interface ConverterAction {
    data class OnAmountChanged(
        val value: String
    ) : ConverterAction

    data class FromSelectedCode(
        val code: String
    ) : ConverterAction

    data class ToSelectedCode(
        val code: String
    ) : ConverterAction

    data object OnSwapClicked : ConverterAction

    data object OnRetryClicked : ConverterAction
}