package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.Screen

@Composable
fun DismissibleCard(
    navController: NavController,
    title: String,
    description: String,
    imageRes: Int
) {
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(360.dp)
                .clickable(onClick = {
                    navController.navigate(Screen.Reminder.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Close button in the top right corner
                IconButton(
                    onClick = { isVisible = false },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().padding(top = 40.dp)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "medication",
                        modifier = Modifier.size(220.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                 }
            }
        }
    }
}

@Preview
@Composable
fun CardPreview() {
    val navController = rememberNavController()
    DismissibleCard(
        navController = navController,
        title = "Track Your Medications",
        description = "Set reminders and track your medication intake all in one place.",
        imageRes = R.drawable.remedy_rafiki
    )
}
