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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    // Add animation for pulse visualization
    val pulseAnimation = rememberInfiniteTransition()
    val pulseScale by pulseAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Calculate time since last pulse for animation
    val timeSinceLastPulse = System.currentTimeMillis() - lastPulseTimestamp
    val isPulseRecent = timeSinceLastPulse < 1000 // Pulse detected in last second
    
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
            
            // Draw baseline
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(0f, midY),
                end = Offset(width, midY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
            )
            
            if (ppgData.isNotEmpty()) {
                // Find min and max values for better scaling
                val minValue = ppgData.minOrNull() ?: -1.0
                val maxValue = ppgData.maxOrNull() ?: 1.0
                val valueRange = (maxValue - minValue).coerceAtLeast(0.5) // Ensure minimum range
                
                // Create main waveform path
                val path = Path()
                val pointCount = ppgData.size.coerceAtMost(150)
                val dataSize = ppgData.size
                
                // Calculate peaks for highlighting
                val peaks = mutableListOf<Int>()
                if (ppgData.size > 3) {
                    for (i in 1 until ppgData.size - 1) {
                        if (ppgData[i] > ppgData[i-1] && ppgData[i] > ppgData[i+1] && 
                            ppgData[i] > (minValue + valueRange * 0.6)) {
                            peaks.add(i)
                        }
                    }
                }
                
                // Draw the waveform
                path.moveTo(0f, midY)
                
                for (i in 0 until pointCount) {
                    val x = i * width / pointCount
                    val dataIndex = (dataSize - pointCount + i).coerceAtLeast(0)
                    val dataPoint = ppgData[dataIndex]
                    
                    // Improved amplitude scaling based on signal range
                    val normalizedValue = (dataPoint - minValue) / valueRange
                    val amplitudeFactor = height * 0.4f // Use 80% of available height
                    val y = midY - (normalizedValue * amplitudeFactor).toFloat()
                    
                    if (i > 0) {
                        path.lineTo(x, y)
                    } else {
                        path.moveTo(x, y)
                    }
                }
                
                // Draw the main waveform with enhanced styling
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                
                // Highlight detected peaks
                for (peakIndex in peaks) {
                    // Only draw peaks that are within our visible window
                    if (peakIndex >= dataSize - pointCount) {
                        val visibleIndex = peakIndex - (dataSize - pointCount)
                        val x = visibleIndex * width / pointCount
                        val dataPoint = ppgData[peakIndex]
                        val normalizedValue = (dataPoint - minValue) / valueRange
                        val y = midY - (normalizedValue * height * 0.4f).toFloat()
                        
                        drawCircle(
                            color = lineColor.copy(alpha = 0.7f),
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
                
                // Add pulse indicator when a pulse is detected
                if (isPulseRecent && isFingerDetected) {
                    // Draw a pulsating circle at the latest data point
                    val latestDataIndex = dataSize - 1
                    val latestDataPoint = ppgData[latestDataIndex]
                    val normalizedValue = (latestDataPoint - minValue) / valueRange
                    val latestY = midY - (normalizedValue * height * 0.4f).toFloat()
                    val latestX = width - 10f // Near the right edge
                    
                    // Draw pulse wave effect
                    for (i in 1..3) {
                        val alpha = 0.7f - (i * 0.2f)
                        val radiusScale = 1f + (i * 0.5f * pulseScale)
                        drawCircle(
                            color = lineColor.copy(alpha = alpha),
                            radius = 6.dp.toPx() * radiusScale,
                            center = Offset(latestX, latestY)
                        )
                    }
                }
            } else {
                // Draw placeholder when no data
                val path = Path()
                path.moveTo(0f, midY)
                path.lineTo(width, midY)
                
                drawPath(
                    path = path,
                    color = Color.Gray.copy(alpha = 0.4f),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Draw text indicating no signal
                drawContext.canvas.nativeCanvas.drawText(
                    "Place finger on camera",
                    width / 2,
                    midY - 20,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 14.sp.toPx()
                    }
                )
            }
        }
    }
}