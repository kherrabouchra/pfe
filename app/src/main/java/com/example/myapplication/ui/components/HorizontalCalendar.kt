package com.example.myapplication.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.text.font.FontWeight
import java.time.DayOfWeek
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HorizontalCalendar(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit
) {
    // Keep track of the first visible week
    var firstVisibleWeekOffset by remember { mutableStateOf(0L) }
    val currentDate = LocalDate.now()
    
    val lazyListState = rememberLazyListState()
    
    // Add formatter for the month and year
    val monthYearFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }
    val displayDate = remember(firstVisibleWeekOffset) {
        currentDate.plusDays(firstVisibleWeekOffset * 7).format(monthYearFormatter)
    }

    Column(modifier = modifier) {
        // Updated navigation row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { firstVisibleWeekOffset-- },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous week"
                )
            }

            Text(
                text = displayDate,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { firstVisibleWeekOffset++ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next week"
                )
            }
        }
        LazyRow(
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                items((-7..7).map { 
                    currentDate.plusDays(it.toLong() + (firstVisibleWeekOffset * 7))
                }) { date ->
                    DateItem(
                        date = date,
                        isSelected = date == selectedDate,
                        onDateSelected = onDateSelected
                    )
                }
            },
            // Add pagination for infinite scrolling
            userScrollEnabled = true,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
        )


    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .border(
                3.dp,
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            ).background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )
            .clickable { onDateSelected(date) }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day of week (M, T, W, etc.) - now in English and uppercase
        Text(
            text = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> "M"
                DayOfWeek.TUESDAY -> "T"
                DayOfWeek.WEDNESDAY -> "W"
                DayOfWeek.THURSDAY -> "T"
                DayOfWeek.FRIDAY -> "F"
                DayOfWeek.SATURDAY -> "S"
                DayOfWeek.SUNDAY -> "S"
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Day of month
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

// Preview
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreviewHorizontalCalendar() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    HorizontalCalendar(
        selectedDate = selectedDate,
        onDateSelected = { selectedDate = it }
    )
}

