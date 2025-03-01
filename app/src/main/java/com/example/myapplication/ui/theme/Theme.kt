package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2547CE),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF4265E7),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF7F7F7),
    onBackground = Color(0xFF212529),
    onSurface = Color(0xFF101214)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF2547CE),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFFB4CAD6),
    onSecondary = Color(0xFF000000),
    background = Color(0xFF000000),
    surface = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF)
)

@Composable
fun BetterAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
} 