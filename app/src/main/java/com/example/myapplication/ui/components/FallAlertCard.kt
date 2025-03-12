package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FallAlertCard(
    message: String,
    onConfirmPhoneFall: () -> Unit,
    onMarkEmergency: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = onConfirmPhoneFall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("It was just a phone fall")
            }

            OutlinedButton(
                onClick = onMarkEmergency,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mark as Emergency")
            }
        }
    }
}


@Preview
@Composable
fun FallAlertCardPreview() {
    FallAlertCard(
        message = "Fall Detected",

        onConfirmPhoneFall = {},
        onMarkEmergency = { }
    )
   
}