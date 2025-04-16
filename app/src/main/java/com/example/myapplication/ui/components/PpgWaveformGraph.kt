package com.example.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.abs

/**
 * A component that displays a real-time PPG waveform graph that animates with each detected pulse.
 * The graph shows a smooth wave animation that represents the blood flow detected by the camera.
 */
@Composable
fun PpgWaveformGraph(
    ppgData: List<Double>,
    lastPulseTimestamp: Long,
    isFingerDetected: Boolean,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFFFF4444),
    backgroundColor: Color = Color(0xFFFAFAFA)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height / 2
            
            if (ppgData.isNotEmpty()) {
                val path = Path()
                val pointCount = ppgData.size.coerceAtMost(150)
                val dataSize = ppgData.size
                
                path.moveTo(0f, midY)
                
                for (i in 0 until pointCount) {
                    val x = i * width / pointCount
                    val dataIndex = (dataSize - pointCount + i).coerceAtLeast(0)
                    val dataPoint = ppgData[dataIndex]
                    
                    // Direct raw data visualization
                    val baseAmplitude = height * 0.45
                    val scaledValue = (dataPoint * baseAmplitude).toFloat()
                    val y = midY - scaledValue
                    
                    if (i > 0) {
                        val prevX = (i - 1) * width / pointCount
                        path.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
            } else {
                val path = Path()
                path.moveTo(0f, midY)
                path.lineTo(width, midY)
                
                drawPath(
                    path = path,
                    color = Color.Gray.copy(alpha = 0.4f),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}