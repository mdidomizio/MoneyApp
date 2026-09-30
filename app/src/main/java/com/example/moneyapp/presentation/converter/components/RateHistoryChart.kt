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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.moneyapp.domain.model.ExchangeRate

@Composable
fun RateHistoryChart(
    history: List<ExchangeRate>,
    modifier: Modifier = Modifier,
) {
    if (history.size < 2) return

    val points = remember(history) { history.map { it.rate.toFloat() } }
    val low = remember(history) { history.minOf { it.rate } }
    val high = remember(history) { history.maxOf { it.rate } }
    val lineColor = MaterialTheme.colorScheme.onSecondary

    Column(modifier) {
        Text("Last 30 days", style = MaterialTheme.typography.titleSmall)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(MaterialTheme.colorScheme.secondary)
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
            Text("Low ${low.stripTrailingZeros().toPlainString()}", style = MaterialTheme.typography.bodySmall)
            Text("High ${high.stripTrailingZeros().toPlainString()}", style = MaterialTheme.typography.bodySmall)
        }
    }
}