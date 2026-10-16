package com.example.moneyapp.presentation.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneyapp.R
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.presentation.converter.components.CurrencyPickerSheet
import com.example.moneyapp.presentation.converter.components.ErrorComponent
import com.example.moneyapp.presentation.converter.components.SuccessComponent
import com.example.moneyapp.presentation.util.formatted
import com.example.moneyapp.presentation.util.toMessageRes
import com.example.moneyapp.ui.theme.MoneyAppTheme
import java.math.BigDecimal
import java.time.LocalDate

private enum class PickerTarget { FROM, TO }

@Composable
fun ConverterRoot(viewModel: ConverterViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ConverterScreen(state = state, onAction = viewModel::onAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    state: ConverterUiState,
    onAction: (ConverterAction) -> Unit,
) {
    var pickerTarget by remember { mutableStateOf<PickerTarget?>(null) }
    val locale = LocalConfiguration.current.locales[0]
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.converter_screen_title)
                    )
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { onAction(ConverterAction.OnAmountChanged(it)) },
                label = {
                    Text(stringResource(R.string.converter_amount_label, state.from))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalButton(
                    onClick = { pickerTarget = PickerTarget.FROM },
                    modifier = Modifier.weight(1f),
                ) { Text(state.from) }

                TextButton(onClick = { onAction(ConverterAction.OnSwapClicked) }) {
                    Text("⇄", style = MaterialTheme.typography.titleLarge)
                }

                FilledTonalButton(
                    onClick = { pickerTarget = PickerTarget.TO },
                    modifier = Modifier.weight(1f),
                ) { Text(state.to) }
            }



            when (val rateState = state.rateState) {
                RateUiState.Loading -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                is RateUiState.Error -> {
                    ErrorComponent(
                        onAction = { onAction(ConverterAction.OnRetryClicked) },
                        errorMessage = stringResource(rateState.errorMessage.toMessageRes()),
                        modifier = Modifier
                    )
                }

                is RateUiState.Success -> {
                    SuccessComponent(
                        modifier = Modifier,
                        from = state.from,
                        to = state.to,
                        convertedAmount = state.converted?.formatted(locale) ?: "–",
                        rateState = rateState,
                        locale = locale,
                    )
                }
            }
        }





        }
    pickerTarget?.let { target ->
        CurrencyPickerSheet(
            currencies = state.currencies,
            onSelect = { code ->
                onAction(
                    if (target == PickerTarget.FROM) ConverterAction.FromSelectedCode(code)
                    else ConverterAction.ToSelectedCode(code)
                )
                pickerTarget = null
            },
            onDismiss = { pickerTarget = null }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConverterScreenPreview() {
    MoneyAppTheme {
        ConverterScreen(
            state = ConverterUiState(
                amountInput = "100",
                rateState = RateUiState.Success(
                    rate = ExchangeRate(
                        "EUR",
                        "USD",
                        BigDecimal("1.1568"),
                        LocalDate
                            .of(2026, 9, 29)
                    ),
                    history = emptyList(),
                    )
            ),
            onAction = {},
        )
    }
}