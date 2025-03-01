package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StepsProgressArc(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = Color(0xFFE0E0E0)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f)
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            // Background arc
            drawArc(
                color = backgroundColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(
                    width = 24.dp.toPx(),
                    cap = StrokeCap.Round
                ),
                size = Size(size.width, size.height * 2f),
                topLeft = Offset(0f, 0f)
            )

            // Progress arc
            drawArc(
                color = progressColor,
                startAngle = 180f,
                sweepAngle = 180f * progress,
                useCenter = false,
                style = Stroke(
                    width = 30.dp.toPx(),
                    cap = StrokeCap.Round
                ),
                size = Size(size.width, size.height * 2f),
                topLeft = Offset(0f, 0f),

            )

            // Draw tick marks
            val tickCount = 20
            val tickLength = 12.dp.toPx()
            val radius = (size.width / 2f)
            val centerX = size.width / 2f
            val centerY = size.height

            for (i in 0..tickCount) {
                val angle = PI + (PI * i / tickCount)
                val startX = centerX + (radius - tickLength) * cos(angle).toFloat()
                val startY = centerY + (radius - tickLength) * sin(angle).toFloat()
                val endX = centerX + radius * cos(angle).toFloat()
                val endY = centerY + radius * sin(angle).toFloat()

                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
} 