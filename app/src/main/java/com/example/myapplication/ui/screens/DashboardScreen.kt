package com.example.myapplication.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Phone
import com.example.myapplication.R
import com.example.myapplication.ui.components.HealthMetricCard
import com.example.myapplication.ui.components.StepsCard
import com.example.myapplication.viewmodel.MainViewModel
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.example.chat.AIChatScreen
import com.example.myapplication.ui.components.RecommendationCard
import kotlin.math.roundToInt
import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.example.myapplication.navigation.Screen
import androidx.navigation.compose.rememberNavController
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit,
    navController: NavController
) {
    var currentRoute by remember { mutableStateOf("home") }
    var moodValue by remember { mutableStateOf(0f) }
    var checkedStates by remember { mutableStateOf(listOf(true, false, false)) }
    var showChat by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),

            floatingActionButton = {

                FloatingActionButton(
                 onClick = { showChat = true },

       ) {
             Icon(
                      imageVector = Icons.Default.Phone,
                  contentDescription = "Emergency call"
                  )
              }

            },
            floatingActionButtonPosition = FabPosition.End
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)

                    .verticalScroll(rememberScrollState()) // Added scroll here
            ) {
               TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                 horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.better_logo),
                                        contentDescription = "logo",
                                        modifier = Modifier.size(70.dp),
                                        alignment = Alignment.CenterStart
                                    )

                                }
                                IconButton(
                                    onClick = { /* TODO: Handle notifications */ }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_notif),
                                        contentDescription = "Notifications"
                                        ,modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                Column (modifier = Modifier.padding(16.dp)){

                    Image(
                        modifier = Modifier.width(250.dp),
                        painter = painterResource(id =R.drawable.undraw_quiet_street_v45k),
                        contentDescription = "Quiet street",
                        alignment = Alignment.Center
                    )

                Text(
                    text = "How are you feeling today?",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 16.dp),
                    fontWeight = FontWeight.Bold,
                )

                // Mood Selection with Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                    }

                    Slider(
                        value = moodValue,
                        onValueChange = { moodValue = it },
                        valueRange = 0f..2f, // Adjusted to allow discrete 0, 1, 2 values
                        steps = 1, // Prevent values like 1.5
                        onValueChangeFinished = {
                            moodValue = moodValue.roundToInt().toFloat() // Round to nearest integer
                        },












                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Awful",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (moodValue < 0.33f) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Neutral",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (moodValue in 0.33f..0.66f) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Good",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (moodValue > 0.66f) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Health Summary Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Health Summary",
                        style = MaterialTheme.typography.titleMedium,

                    )
                    Text(
                        text = "Edit",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.clickable { /* TODO: Handle edit */ }
                    )
                }

                // Health Metrics Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    HealthMetricCard(
                        title = "Heart rate",
                        value = "69",
                        unit = "BPM",
                        subtitle = "Latest",
                        icon = R.drawable.ic_ecg,
                        modifier = Modifier.weight(1f)
                    )
                    HealthMetricCard(
                        title = "HRV",
                        value = "50",
                        unit = "MS",
                        subtitle = "Latest",
                        icon = R.drawable.ic_hrv,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Steps Card
                StepsCard(
                    steps = 2601,
                    goal = 5000,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                // Activity Log with checkable items and strike-through
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                Text(
                    text = "Activity Log",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "View All",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.clickable { /* TODO: Handle edit */ }
                    )
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    // Vertical Blue Line
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(120.dp)
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.CenterStart)
                            .offset(x = 8.dp)
                    )

                    Column {
                        checkedStates.forEachIndexed { index, checked ->
                            ActivityLogItem(
                                text = when (index) {
                                    0 -> "Hygiene Completed at 7:00 am"
                                    1 -> "Take Aspirin at 08:30 pm"
                                    else -> "Record Vitals at 2:00 pm"
                                },
                                isCompleted = checked,
                                onCheckedChange = { newValue ->
                                    checkedStates = checkedStates.toMutableList().also {
                                        it[index] = newValue
                                    }
                                }
                            )
                        }
                    }
                }

                // Recommendations
                Text(
                    text = "Recommendations",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )

                RecommendationCard(
                    title = "Try a 5-min walk to boost mood!",
                    subtitle = "Your last three mood logs were neutral."
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecommendationCard(
                    title = "Would you like to record vitals?",
                    subtitle = "You've missed recording vitals for 2 days."
                )
            }
        } }

        // Updated Bottom Navigation
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            val items = listOf(
                Triple("Home", R.drawable.home, "home"),
                Triple("AI Assistant", android.R.drawable.stat_notify_chat, "AIChat"),
                Triple("Settings", R.drawable.settings, "settings")
            )

            items.forEach { (label, icon, route) ->
                NavigationBarItem(
                    selected = currentRoute == route,
                    onClick = {
                        if (route == "AIChat") {
                            navController.navigate(Screen.AIChat.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = true }
                            }
                        } else if (route == "settings") {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = true }
                            }
                        } else {
                            currentRoute = route
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        indicatorColor = Color.White
                    ),
                    modifier = Modifier.background(
                        if (currentRoute == route)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Unspecified
                    )
                )
            }
        }
    }

    if (showChat) {
        AIChatScreen(navController = navController)
    }
}

@Composable
private fun ActivityLogItem(
    text: String,
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isCompleted,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
        Text(
            text = text,
            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .then(if (isCompleted) Modifier.padding(start = 6.dp) else Modifier),
            color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun DashboardScreenPreview() {
    // Create a mock NavController for preview purposes
    val navController = rememberNavController()
    DashboardScreen(viewModel = MainViewModel(), onSignOut = {}, navController = navController)
}
