package com.example.myapplication.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.screens.WaterScreen

@Composable
fun WaterCupGrid(
    modifier: Modifier = Modifier,
    onCupClick: (Int) -> Unit = {},
    dailyGoal: Int = 2500, // ml
    cupSize: Int = 250, // ml
    currentIntake: Int = 0
) {
    var selectedCup by remember { mutableStateOf(currentIntake / cupSize) }
    val waterColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
       Column  {
           LinearProgressIndicator(
               progress = { (selectedCup * cupSize / dailyGoal.toFloat()).coerceIn(0f, 1f) },
               modifier = Modifier
                   .fillMaxWidth()
                   .height(18.dp)
                   .padding(bottom = 8.dp),
               trackColor = Color.LightGray.copy(alpha = 0.5f)
           )

           // Add water intake text
           Text(
               text = "${selectedCup * cupSize}ml / ${dailyGoal}ml",
               style = MaterialTheme.typography.bodyMedium,
               color = Color.Gray,
               modifier = Modifier.padding(bottom = 16.dp)
           )

       }

        Column (horizontalAlignment = Alignment.CenterHorizontally){
        // Create 2 rows with 4 cups each
        for (row in 0..2) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement =  Arrangement.Center
            ) {
                // Create 4 cups per row
                for (col in 0 until if (row < 2) 4 else 2) {
                    val cupIndex = row * 4 + col
                    val isFilled = cupIndex < selectedCup

                    // Cup container with proper cup illustration
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable {
                                selectedCup = cupIndex + 1
                                onCupClick(cupIndex)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Cup illustration
                        if (isFilled) {
                            // Filled cup
                            Icon(
                                painter = painterResource(id = R.drawable.water_cup_filled),
                                contentDescription = "Water cup filled",
                                modifier = Modifier.size(80.dp),
                                tint = Color.Unspecified
                            )
                        } else {
                            // Empty cup
                            Icon(
                                painter = painterResource(id = R.drawable.water_cup_illustration),
                                contentDescription = "Water cup empty",
                                modifier = Modifier.size(80.dp),
                                tint = Color.Unspecified
                            )
                            
                            // Plus icon (only visible when empty)
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add water",
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }}
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun WaterCupGridPreview() {
    val navController = rememberNavController()
    WaterCupGrid()
}