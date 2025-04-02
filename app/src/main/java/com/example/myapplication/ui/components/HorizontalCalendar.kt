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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
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
    val dayMonthFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM") }
    val yearFormatter = remember { DateTimeFormatter.ofPattern("yyyy") }

    val monthYearFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }
    val displayDate = remember(firstVisibleWeekOffset) {
        currentDate.plusDays(firstVisibleWeekOffset * 7).format(monthYearFormatter)
    }
Column (modifier = Modifier.fillMaxWidth()) {
    Spacer(modifier = Modifier.height(10.dp))
    Row (verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()){

        Text(
            text= "Today,",
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Text(
            text= "${currentDate.format(dayMonthFormatter)}\n${currentDate.format(yearFormatter)}",
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 10.dp),
            color = Color.Black.copy(alpha = 0.3f),
            textAlign = TextAlign.End
        )
    }


    Column(modifier = modifier.padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

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
        // Updated navigation row
        Column(modifier = Modifier.padding(6.dp).clip(RoundedCornerShape(16.dp))){
        Row(
            modifier = Modifier
                .background(Color.LightGray.copy(alpha = 0.3f)).fillMaxWidth(0.95f)
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
                        contentDescription = "Previous week",
                        modifier =  Modifier.requiredSize(30.dp)
                    )
                }

                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(5f),
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = { firstVisibleWeekOffset++ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next week",
                       modifier =  Modifier.requiredSize(30.dp)
                    )
                }
            }

        }



    }
}}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DateItem(
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(38.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                else Color.Transparent
            )
            .clickable { onDateSelected(date) }
            .padding(vertical = 8.dp, horizontal = 10.dp)
            ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // Day of week (M, T, W, etc.) - now in English and uppercase
        Text(
            text = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> "Mon"
                DayOfWeek.TUESDAY -> "Tue"
                DayOfWeek.WEDNESDAY -> "Wed"
                DayOfWeek.THURSDAY -> "Thu"
                DayOfWeek.FRIDAY -> "Fri"
                DayOfWeek.SATURDAY -> "Sat"
                DayOfWeek.SUNDAY -> "Sun"
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Black
            ),
            color = if (isSelected) Color.White else Color.Gray
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Day of month
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) Color.White else Color.Gray,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun HorizontalCalendarPreview() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    HorizontalCalendar(
        selectedDate = selectedDate,
        onDateSelected = { selectedDate = it }
    )
}

