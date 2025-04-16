package com.example.myapplication.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.HeartRateResult

@Composable
fun HeartRateGraph(
    heartRateData: List<HeartRateResult>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(18.dp)
    ) {
        if (heartRateData.isEmpty()) {
            Text(
                text = "No heart rate data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val padding = 30f

                val minHeartRate = heartRateData.minOf { it.heartRate }.toFloat()
                val maxHeartRate = heartRateData.maxOf { it.heartRate }.toFloat()
                val yRange = (maxHeartRate - minHeartRate).coerceAtLeast(20f)

                val yStep = yRange / 4

                for (i in 0..4) {
                    val y = height - (i * height / 4) - padding
                    val value = minHeartRate + (i * yStep)

                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(padding, y),
                        end = Offset(width - padding, y),
                        strokeWidth = 2f
                    )

                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            value.toInt().toString(),
                            padding - 25f,
                            y + 5f,
                            Paint().apply {
                                color = android.graphics.Color.GRAY
                                textSize = 32f
                                textAlign = Paint.Align.RIGHT
                            }
                        )
                    }
                }

                if (heartRateData.size > 1) {
                    val path = Path()
                    val points = heartRateData.mapIndexed { index, result ->
                        val x = padding + (index * (width - 2 * padding) / (heartRateData.size - 1))
                        val y = height - padding - ((result.heartRate - minHeartRate) / yRange * (height - 2 * padding))
                        Offset(x, y)
                    }

                    path.moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        path.lineTo(points[i].x, points[i].y)
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    points.forEach { point ->
                        drawCircle(
                            color = lineColor,
                            radius = 4.dp.toPx(),
                            center = point
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeartRateGraphPreview() {
    val sampleData = listOf(
        HeartRateResult(timestamp = 0L.toString(), heartRate = 72, status = "Normal", confidenceLevel = 0.9f.toString()),
        HeartRateResult(timestamp = 1L.toString(), heartRate = 76, status = "Normal", confidenceLevel = 0.88f.toString()),
        HeartRateResult(timestamp = 2L.toString(), heartRate = 80, status = "Elevated", confidenceLevel = 0.92f.toString()),
        HeartRateResult(timestamp = 3L.toString(), heartRate = 75, status = "Normal", confidenceLevel = 0.85f.toString()),
        HeartRateResult(timestamp = 4L.toString(), heartRate = 78, status = "Normal", confidenceLevel = 0.87f.toString()),
        HeartRateResult(timestamp = 5L.toString(), heartRate = 74, status = "Normal", confidenceLevel = 0.89f.toString()),
        HeartRateResult(timestamp = 6L.toString(), heartRate = 70, status = "Normal", confidenceLevel = 0.91f.toString())
    )

    HeartRateGraph(
        heartRateData = sampleData,
        modifier = Modifier.padding(16.dp)
    )
}
