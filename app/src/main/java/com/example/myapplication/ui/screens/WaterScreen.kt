package com.example.myapplication.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.HorizontalCalendar
import com.example.myapplication.ui.components.WaterCupGrid
import com.example.myapplication.ui.components.WaterIntakeCard
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WaterScreen(
    navController: NavController
) {
    var currentRoute by remember { mutableStateOf("water") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var waterIntake by remember { mutableIntStateOf(0) }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                navController = navController,
                onRouteChange = { currentRoute = it }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar with back button and title
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .background(color = Color.LightGray.copy(alpha = 0.1f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("activities") }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.requiredSize(30.dp)
                    )
                }
                
                Text(
                    text = "Water Intake",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
            
            HorizontalDivider(
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            
            // Horizontal Calendar
            HorizontalCalendar(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(6.dp))
            
            // Water Cup Grid
            WaterCupGrid(
                onCupClick = { cupIndex ->
                    // Each cup represents 250ml
                    waterIntake = (cupIndex + 1) * 250
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Description Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_water),
                            contentDescription = "Water Info",
                            modifier = Modifier.requiredSize(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        
                        Text(
                            text = "Hydration Benefits",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Staying properly hydrated is essential for your health. Water helps maintain body temperature, lubricates joints, protects sensitive tissues, and helps eliminate waste through urination, perspiration, and bowel movements.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "The recommended daily water intake is about 2.5 liters (8-10 cups) for adults. Track your water consumption throughout the day to ensure you're meeting your hydration goals.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun WaterScreenPreview() {
    val navController = rememberNavController()
    WaterScreen(navController = navController)
}