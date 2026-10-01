package com.example.moneyapp.presentation.converter.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moneyapp.R
import com.example.moneyapp.domain.model.ExchangeRate
import com.example.moneyapp.presentation.util.formattedChange
import com.example.moneyapp.presentation.util.formattedRate
import java.math.MathContext
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RateHistoryChart(
    history: List<ExchangeRate>,
    modifier: Modifier = Modifier,
) {
    if (history.size < 2) return

    val points = remember(history) { history.map { it.rate.toFloat() } }
    val low = remember(history) { history.minOf { it.rate } }
    val high = remember(history) { history.maxOf { it.rate } }
    val lineColor = MaterialTheme.colorScheme.onSecondaryContainer
    val change = remember(history) {
        val first = history.first().rate
        val last = history.last().rate
        (last - first).divide(first, MathContext.DECIMAL64)
    }

    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.rate_history_chart_coverage),
                style = MaterialTheme.typography.titleSmall
            )
            val sign = if (change.signum() > 0) "+" else ""
            Text(
                text = change.formattedChange(),
                style = MaterialTheme.typography.titleSmall
            )
        }


        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(12.dp),
        ) {
            val min = points.min()
            val range = (points.max() - min).takeIf { it > 0f } ?: 1f
            val stepX = size.width / (points.size - 1)

            val path = Path().apply {
                points.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = size.height - ((value - min) / range) * size.height
                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.rate_history_chart_low, low.formattedRate()),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.rate_history_chart_high, high.formattedRate()),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}