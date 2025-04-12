package com.example.myapplication.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.ActivityCard
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.NotificationCard
import com.example.myapplication.viewmodel.MainViewModel
@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActivitiesScreen(navController: NavController) {
    var currentRoute by remember { mutableStateOf("activities") }
    var isEditMode by remember { mutableStateOf(false) }
    val activities = remember {
        mutableStateListOf(
            "medication", "stepcounter", "water", "nutrition", "sleep", "vitals"
        )
    }
    val selectedActivities = remember { mutableStateListOf<String>().apply { addAll(activities) } }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color.LightGray),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                navController = navController,
                onRouteChange = { currentRoute = it }
            )
        }
    ) { padding ->

        Column {
            // Header
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .background(color = Color.LightGray.copy(alpha = 0.1f)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("Dashboard") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Go Back", modifier = Modifier.requiredSize(30.dp))
                }
                Text(
                    text = "My Activities",
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

            // Edit Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { isEditMode = !isEditMode }) {
                    Text(
                        text = if (isEditMode) "Save" else "Edit",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                val allActivities = listOf(
                    Triple("Medication", R.drawable.pill, "medication"),
                    Triple("Mobility", R.drawable.walk_ic, "stepcounter"),
                    Triple("Water", R.drawable.bottle_of_water_rafiki, "water"),
                    Triple("Nutrition", R.drawable.healthy_food_rafiki, "nutrition"),
                    Triple("Sleep", R.drawable.ic_sleep, "sleep"),
                    Triple("Vitals", R.drawable.hand, "vitals")
                )
                for ((title, iconId, route) in allActivities) {
                    val isSelected = selectedActivities.contains(route)

                    if (isEditMode || isSelected) {
                        ActivityCard(
                            title = title,
                            desc = when (route) {
                                "medication" -> "Set your reminders."
                                "stepcounter" -> "Your activity trends."
                                "water" -> "Track your water intake."
                                "nutrition" -> "Log your meals."
                                "sleep" -> "Track your sleep patterns."
                                "vitals" -> "Monitor your vitals."
                                else -> ""
                            },
                            icon = painterResource(id = iconId),
                            navigate = when (route) {
                                "medication" -> Screen.Medication.route
                                "stepcounter" -> Screen.StepCounter.route
                                "water" -> Screen.Water.route
                                "nutrition" -> Screen.Nutrition.route
                                "sleep" -> Screen.Sleep.route
                                "vitals" -> Screen.Vitals.route
                                else -> route
                            },
                            navController = navController,
                            isEditMode = isEditMode,
                            isSelected = isSelected,
                            onSelectToggle = {
                                if (isSelected) selectedActivities.remove(route) else selectedActivities.add(route)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }


                Spacer(modifier = Modifier.height(242.dp))
            }
        }
    }
}


@Preview
@Composable
fun ActivitiesScreenPreview() {
    val navController = rememberNavController()
    ActivitiesScreen(navController = navController)
}
