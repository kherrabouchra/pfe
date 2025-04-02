package com.example.myapplication.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.HorizontalCalendar
import com.example.myapplication.ui.components.SleepBar
import com.example.myapplication.ui.components.TargetMetCard
import com.example.myapplication.ui.components.TargetMetCardPreview

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SleepScreen(navController: NavController) {
    var currentRoute by remember { mutableStateOf("activities") }
    var selectedTab by remember { mutableStateOf(0) }
    var selectedTimeframe by remember { mutableStateOf("Weekly") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
        ) {
            // Header with back button
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("activities") }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Sleep Activity",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState()))
            {

                HorizontalCalendar { }
                Row(modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Column (modifier = Modifier.weight(2F)){
                        Text(
                            text = "Track Your Sleep Patterns",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            text = "Set reminders and take your medications on time.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Start,

                        )
                        Button(
                            onClick = {  },
                            modifier = Modifier.height(42.dp).padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Add Reminder"

                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1.6f)) {
                        Image(
                            painter = painterResource(id = R.drawable.sleep_analysis_rafiki),
                            contentDescription = "medication",
                            modifier = Modifier.size(130.dp).requiredSize(220.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Tab navigation (Total, Count, Average)
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    },
                    divider = {}
                ) {
                    listOf("Total", "Count", "Average").forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                            modifier = Modifier.background(
                                if (selectedTab == index) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.1f
                                ) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
if (selectedTab == 0){

    // Sleep time chart card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Sleep Time",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Daily activity",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Dropdown for Weekly selection
                OutlinedButton(
                    onClick = { /* Toggle dropdown */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(selectedTimeframe)
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select timeframe",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bar chart visualization
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Monday bar
                SleepBar(height = 0.3f, day = "Mon", isSelected = false)

                // Tuesday bar
                SleepBar(height = 0.6f, day = "Tue", isSelected = false)

                // Wednesday bar
                SleepBar(height = 0.4f, day = "Wed", isSelected = false)

                // Thursday bar (selected/highlighted)
                SleepBar(
                    height = 0.9f,
                    day = "Thu",
                    isSelected = true,
                    hours = "08:30 h"
                )

                // Friday bar
                SleepBar(height = 0.3f, day = "Fri", isSelected = false)

                // Saturday bar
                SleepBar(height = 0.6f, day = "Sat", isSelected = false)

                // Sunday bar
                SleepBar(height = 0.4f, day = "Sun", isSelected = false)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Text(
                    text = "Today",
                    modifier = Modifier.padding(start = 4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.LightGray, CircleShape)
                )
                Text(
                    text = "Your Baby",
                    modifier = Modifier.padding(start = 4.dp),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "8 h 30 min",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Sleep score card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sleep Score",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                // Dropdown for Weekly selection
                OutlinedButton(
                    onClick = { /* Toggle dropdown */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(selectedTimeframe)
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select timeframe",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Circular progress indicator
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = 0.88f,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 16.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color(0xFFE0E0E0)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "88%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Score",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }


}else if (selectedTab == 2) {
    TargetMetCard{}

}
                Spacer(modifier = Modifier.height(240.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun SleepScreenPreview() {
    val navController = rememberNavController()
    SleepScreen(navController = navController)
}