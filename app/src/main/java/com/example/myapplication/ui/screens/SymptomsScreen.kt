package com.example.myapplication.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SymptomsScreen(
    navController: NavController
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf("activities") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(46.dp)).background(Color.Black)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Symptom",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
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
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = { navController.navigate("Dashboard") }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.requiredSize(30.dp)
                    )
                }

                Text(
                    text = "Symptoms",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .fillMaxWidth().align(Alignment.CenterHorizontally)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                HorizontalCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
                
                Row(modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Column (modifier = Modifier.weight(1F) ){
                        Text(
                            text = "Track Your Symptoms",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            text = "Record and monitor your symptoms over time.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        Button(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier.height(44.dp).padding(top = 10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Log Symptoms")
                        }
                    }
                    Column(modifier = Modifier.weight(1f) ) {
                        Image(
                            painter = painterResource(id = R.drawable.thermometer_rafiki),
                            contentDescription = "symptoms",
                            modifier = Modifier.size(140.dp).requiredSize(240.dp).offset(20.dp)
                        )
                    }
                }
                
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
                        Text(
                            text = "Why Track Symptoms?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Tracking your symptoms helps you and your healthcare provider understand patterns in your health. Regular monitoring can lead to better management of chronic conditions and early detection of potential health issues.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Use this tracker to record symptoms like pain, fatigue, dizziness, or any other health concerns you may experience.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun SymptomsScreenPreview() {
    val navController = rememberNavController()
    SymptomsScreen(navController = navController)
}