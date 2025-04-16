package com.example.myapplication.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.HeartRateResult
import com.example.myapplication.ui.components.CircularIndicator
import com.example.myapplication.ui.components.PercentageRow
import com.example.myapplication.ui.components.SectionCard
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Creates a sample heart rate result for preview purposes
 */
private fun createSampleHeartRateResult(): HeartRateResult {
    val heartRate = Random.nextInt(55, 105)
    val status = when {
        heartRate < 60 -> "Low"
        heartRate > 100 -> "High"
        else -> "Normal"
    }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val timestamp = dateFormat.format(Date())
    val confidenceLevel = listOf("Low", "Medium", "High").random()
    
    return HeartRateResult(
        heartRate = heartRate,
        status = status,
        timestamp = timestamp,
        confidenceLevel = confidenceLevel
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeartRateResultScreen(
    navController: NavController,
    heartRateResult: HeartRateResult? = null
) {
    // If no result is provided, create a sample one for preview
    val result = heartRateResult ?: createSampleHeartRateResult()
    
    // Calculate overall score based on heart rate and confidence level
    val overallScore = when(result.confidenceLevel) {
        "High" -> 90
        "Medium" -> 70
        else -> 50
    }
    
    // Only show metrics we can reliably calculate from the PPG measurement
    val heartRateStatus = when {
        result.heartRate < 60 -> "Low"
        result.heartRate > 100 -> "High"
        else -> "Normal"
    }
    
    // Calculate basic heart rate variability (if available in the result)
    val hrvValue = 50.0 // Default HRV value since it's not in the data class
    
    // Helper functions for status colors and descriptions
    fun getStatusColor(status: String): Color {
        return when (status.lowercase()) {
            "low", "bradycardia" -> Color(0xFF2196F3) // Blue
            "high", "tachycardia" -> Color(0xFFF44336) // Red
            else -> Color(0xFF8BC34A) // Green
        }
    }

    fun getQualityColor(confidenceLevel: String): Color {
        return when (confidenceLevel) {
            "High" -> Color(0xFF8BC34A)
            "Medium" -> Color(0xFFFFA726)
            else -> Color(0xFFF44336)
        }
    }

    fun getHeartConditionText(status: String): String {
        return when (status.lowercase()) {
            "low" -> "Low Heart Rate"
            "high" -> "High Heart Rate"
            else -> "Healthy Heart Rate"
        }
    }

    fun getHeartConditionDescription(status: String): String {
        return when (status.lowercase()) {
            "low" -> "Your heart rate is below normal range"
            "high" -> "Your heart rate is above normal range"
            else -> "Your heart rate is within the healthy range"
        }
    }

    fun getHeartRateAdvice(heartRate: Int): String {
        return when {
            heartRate < 60 -> "Consulting a healthcare provider about your low heart rate"
            heartRate > 100 -> "Consulting a healthcare provider about your high heart rate"
            else -> "Your heart rate is in a healthy range. Keep up the good work!"
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Heart Rate Results", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("vitals") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Overall Score Section - Improved visual hierarchy
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getHeartConditionText(result.status),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = getStatusColor(result.status),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Score circle with improved visual
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { overallScore / 100f },
                                modifier = Modifier.size(120.dp),
                                strokeWidth = 10.dp,
                                trackColor = Color.LightGray,
                                color = when {
                                    overallScore >= 80 -> Color(0xFF4CAF50) // Green
                                    overallScore >= 60 -> Color(0xFFFFA726) // Orange
                                    else -> Color(0xFFF44336) // Red
                                }
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$overallScore",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "SCORE",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Description with improved styling
                        Column(
                            modifier = Modifier.padding(start = 16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = getHeartConditionDescription(result.status),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Heart Rate",
                                    tint = getStatusColor(heartRateStatus),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "  ${result.heartRate} BPM",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = getStatusColor(heartRateStatus)
                                )
                            }

                            Text(
                                text = "Measured: ${result.timestamp}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }


                // Summary Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Heart Rate Status
                            CircularIndicator(
                                value = result.heartRate,
                                label = "Heart Rate",
                                color = getStatusColor(heartRateStatus),
                                description = "${result.heartRate} BPM"
                            )

                            // Measurement Quality
                            CircularIndicator(
                                value = when (result.confidenceLevel) {
                                    "High" -> 90
                                    "Medium" -> 70
                                    else -> 50
                                },
                                label = "Quality",
                                color = getQualityColor(result.confidenceLevel),
                                description = "${result.confidenceLevel}"
                            )

                            // HRV (if available)
                            CircularIndicator(
                                value = hrvValue.toInt(),
                                label = "HRV",
                                color = MaterialTheme.colorScheme.primary,
                                description = "${hrvValue.toInt()} ms"
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.LightGray.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Advice",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Text(
                                    text = getHeartRateAdvice(result.heartRate),
                                    fontSize = 14.sp,
                                )
                            }
                        }
                    }


                    // Pulse Section - Simplified and improved
                    SectionCard(title = "Heart Rate Details") {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Heart rate value
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${result.heartRate}",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = getStatusColor(heartRateStatus)
                                    )

                                    Text(
                                        text = "bpm",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                // Normal range indicator
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.LightGray.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = "Normal: 60-100 bpm",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Heart rate classification
                            Text(
                                text = "Heart Rate Classification",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            PercentageRow(
                                label = "Normal (60-100 bpm)",
                                percentage = 70,
                                color = Color(0xFF8BC34A)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            PercentageRow(
                                label = "High (>100 bpm)",
                                percentage = 20,
                                color = Color(0xFFF44336)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            PercentageRow(
                                label = "Low (<60 bpm)",
                                percentage = 10,
                                color = Color(0xFF2196F3)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Recommendation card - simplified
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                                        alpha = 0.7f
                                    )
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Recommendations",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Regular moderate exercise for 30 minutes daily can help maintain a healthy heart rate.",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // HRV Section - Simplified
                    SectionCard(title = "Heart Rate Variability") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SDNN",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$hrvValue",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = "ms",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                    )
                                }

                                Text(
                                    text = "Average",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Simple HRV visualization
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = "Higher HRV indicates better heart health",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

    } }

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun HeartRateResultScreenPreview() {
    val navController = rememberNavController()
    HeartRateResultScreen(navController = navController)
}