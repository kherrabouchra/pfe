package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.viewmodel.FallDetectionViewModel

@Composable
fun FallDetectionScreen(
    viewModel: FallDetectionViewModel,
    onResetDetection: () -> Unit
) {
    val fallAlertText by viewModel.fallAlertText.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (fallAlertText.isEmpty()) "Fall Detection Active" else fallAlertText,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (fallAlertText.isNotEmpty()) {
                Button(onClick = onResetDetection) {
                    Text("OK")
                }
            }
        }
    }
} 