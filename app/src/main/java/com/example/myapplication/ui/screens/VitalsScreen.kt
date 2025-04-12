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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.DismissibleCard
import com.example.myapplication.ui.components.HealthMetricCard
import com.example.myapplication.ui.components.HorizontalCalendar
import com.example.myapplication.ui.components.NotificationCard
import com.example.myapplication.viewmodel.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VitalsScreen(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf("activities") }
    
    // Collect the last heart rate measurement from the ViewModel
    val lastHeartRate by mainViewModel.lastHeartRate.collectAsState()


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
                    contentDescription = "Add Medication",
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
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
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
                    text = "Vitals",
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

                HorizontalCalendar {}
                Row(modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Column (modifier = Modifier.weight(1F) ){
                        Text(
                            text = "Track Your Vital Signs",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            text = "Set reminders and take your medications on time.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        Button(
                            onClick = { navController.navigate("heart_rate_monitor") },
                            modifier = Modifier.height(44.dp).padding(top = 10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Check heart rate")
                        }
                    }
                    Column(modifier = Modifier.weight(1f) ) {
                        Image(
                            painter = painterResource(id = R.drawable.circulatory_system_rafiki),
                            contentDescription = "medication",
                            modifier = Modifier.size(140.dp).requiredSize(280.dp).offset(20.dp)
                        )
                    }
                }
                
                // Display heart rate card if measurement exists
                if (lastHeartRate != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HealthMetricCard(
                        title = "Heart Rate",
                        value = lastHeartRate!!.heartRate.toString(),
                        unit = "BPM",
                        subtitle = "Last measured: ${lastHeartRate!!.timestamp}",
                        icon = R.drawable.ic_heart, // Using heart icon for heart rate
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { navController.navigate("heart_rate_monitor") }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Status: ${lastHeartRate!!.status}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = when(lastHeartRate!!.status) {
                            "Normal" -> Color.Green
                            "High" -> Color.Red
                            "Low" -> Color(0xFFFFA000) // Amber
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

    }
    }





@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun VitalsScreenPreview() {
    val navController = rememberNavController()
    VitalsScreen(navController = navController)
}
