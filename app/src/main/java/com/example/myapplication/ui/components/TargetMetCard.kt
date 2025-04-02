package com.example.myapplication.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * A reusable component that displays a monthly calendar with colored indicators
 * showing which days the user met their target (green) or missed it (gray).
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TargetMetCard(
    modifier: Modifier = Modifier,
    title: String = "Sleep at least 7 hours a day",
    targetValue: String = "7h 00m",
    averageValue: String = "6h 34m",
    initialMonth: YearMonth = YearMonth.now(),
    daysMetTarget: List<Int> = emptyList(),
    onMonthChanged: (YearMonth) -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(initialMonth) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Adjust for Sunday as first day (0)
    
    // Format month and year
    val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.3.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            
            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                    onMonthChanged(currentMonth)
                }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous month")
                }
                
                Text(
                    text = currentMonth.format(monthYearFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                    onMonthChanged(currentMonth)
                }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next month")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Average and target metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Average",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = averageValue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Target",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = targetValue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Days of week header
            Row(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(240.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                userScrollEnabled = false
            ) {
                // Empty cells for days before the first day of month
                items(firstDayOfWeek) {
                    Box(modifier = Modifier.size(40.dp))
                }
                
                // Days of the month
                items(daysInMonth) { day ->
                    val dayNumber = day + 1
                    val isTargetMet = daysMetTarget.contains(dayNumber)
                    
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isTargetMet) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else Color(0xFFE0E0E0)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            color = if (isTargetMet) Color.White else Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                )
                Text(
                    text = "Goal met",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE0E0E0))
                )
                Text(
                    text = "Goal not met",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TargetMetCardPreview() {
    // Sample data for preview
    val daysMetTarget = listOf(1, 2, 3, 5, 6, 9, 10, 11, 12, 15, 16, 19, 21, 23)
    
    TargetMetCard(
        title = "Sleep at least 7 hours a day",
        targetValue = "7h 00m",
        averageValue = "6h 34m",
        daysMetTarget = daysMetTarget
    )
}