package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodSlider() {
    var moodValue by remember { mutableStateOf(1f) }

    // Choose icon based on mood value
    val moodIcon: ImageVector = when (moodValue.roundToInt()) {
        0 -> Icons.Filled.SentimentDissatisfied // Sad
        1 -> Icons.Filled.Face // Neutral
        2 -> Icons.Filled.Mood // Happy
        else -> Icons.Filled.Face
    }

    Slider(
        value = moodValue,
        onValueChange = { moodValue = it },
        valueRange = 0f..2f,
        steps = 1,
        onValueChangeFinished = {
            moodValue = moodValue.roundToInt().toFloat()
        },
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent, // Hide default thumb
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
        ),
        modifier = Modifier.padding(horizontal = 8.dp),
        thumb = {
            Icon(
                imageVector = moodIcon, // Dynamically selected icon
                contentDescription = "Mood Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}
