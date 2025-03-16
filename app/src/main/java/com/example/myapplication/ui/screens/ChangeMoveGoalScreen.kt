package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChangeMoveGoalScreen(
    currentGoal: Int,
    onGoalChange: (Int) -> Unit,
    onBack: () -> Unit
) {
    var newGoal by remember { mutableStateOf(currentGoal) }

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
                text = "Daily Move Goal",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Set a goal based on how active you are, or how active you'd like to be, each day.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Row for minus and plus buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { if (newGoal > 0) newGoal-- }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Decrease Goal",
                        tint = Color.Blue
                    )
                }
                Text(
                    text = "$newGoal kcal",
                    fontSize = 32.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                IconButton(onClick = { newGoal++ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Increase Goal",
                        tint = Color.Blue
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onGoalChange(newGoal)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text("Save", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangeMoveGoalScreenPreview() {
    ChangeMoveGoalScreen(
        currentGoal = 500,
        onGoalChange = {},
        onBack = {}
    )
}