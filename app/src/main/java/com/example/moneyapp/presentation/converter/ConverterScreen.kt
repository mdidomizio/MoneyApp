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
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.presentation.util.formatted
import com.example.moneyapp.presentation.util.toMessage
import com.example.moneyapp.ui.theme.MoneyAppTheme
import java.math.BigDecimal
import java.time.LocalDate


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
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Currency converter") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            OutlinedTextField(
                value = state.amountInput,
                onValueChange = { onAction(ConverterAction.OnAmountChanged(it)) },
                label = { Text("Amount in ${state.from}") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalButton(
                    onClick = { /* currency picker comes in 5b */ },
                    modifier = Modifier.weight(1f),
                ) { Text(state.from) }

                TextButton(onClick = { onAction(ConverterAction.OnSwapClicked) }) {
                    Text("⇄", style = MaterialTheme.typography.titleLarge)
                }

                FilledTonalButton(
                    onClick = { /* currency picker comes in 5b */ },
                    modifier = Modifier.weight(1f),
                ) { Text(state.to) }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = state.converted?.formatted() ?: "–",
                        style = MaterialTheme.typography.displaySmall,
                    )
                    state.rate
                        ?.takeIf { it.base == state.from && it.quote == state.to }
                        ?.let { rate ->
                            Text(
                                text = "1 ${rate.base} = ${rate.rate.stripTrailingZeros().toPlainString()} ${rate.quote}",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = "Mid-market rate for ${rate.date}. Rates update once per business day.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                }
            }

            state.error?.let { error ->
                Text(error.toMessage(), color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { onAction(ConverterAction.OnRetryClicked) }) {
                    Text("Try again")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConverterScreenPreview() {
    MoneyAppTheme {
        ConverterScreen(
            state = ConverterUiState(
                amountInput = "100",
                rate = ExchangeRate("EUR", "USD", BigDecimal("1.1568"), LocalDate.of(2026, 9, 29)),
            ),
            onAction = {},
        )
    }
}