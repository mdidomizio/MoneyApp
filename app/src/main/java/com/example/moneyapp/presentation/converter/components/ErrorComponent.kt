package com.example.moneyapp.presentation.converter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moneyapp.R

@Composable
fun ErrorComponent(
    modifier: Modifier,
    errorMessage: String,
    onAction: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error
        )
        TextButton(onClick = onAction) {
            Text(
                text = stringResource(R.string.converter_screen_text_button)
            )
        }
    }
}