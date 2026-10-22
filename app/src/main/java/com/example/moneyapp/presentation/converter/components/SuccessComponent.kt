package com.example.moneyapp.presentation.converter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moneyapp.R
import com.example.moneyapp.presentation.converter.RateUiState
import com.example.moneyapp.presentation.util.formattedRate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun SuccessComponent(
    modifier: Modifier,
    convertedAmount: String,
    from: String,
    to: String,
    rateState: RateUiState.Success,
    locale: Locale,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        rateState.savedAt?.let { savedAt ->
            SavedRateBanner(
                savedAt = savedAt,
                locale = locale
            )
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = convertedAmount,
                    style = MaterialTheme.typography.displaySmall,
                )

                rateState.rate
                    ?.takeIf { it.base == from && it.quote == to }
                    ?.let { rate ->
                        Text(
                            text = "1 ${rate.base} = ${
                                rate.rate.formattedRate(locale)
                            } ${rate.quote}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = stringResource(
                                R.string.converter_screen_card_text,
                                rate.date
                                    .format(
                                        DateTimeFormatter
                                            .ofLocalizedDate(FormatStyle.MEDIUM)
                                    )
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
            }
        }
        RateHistoryChart(history = rateState.history)
    }
}