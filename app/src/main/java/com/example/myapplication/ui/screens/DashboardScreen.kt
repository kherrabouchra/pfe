package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.times
import com.example.myapplication.R
import com.example.myapplication.ui.components.HealthMetricCard
import com.example.myapplication.ui.components.RecommendationCard
import com.example.myapplication.ui.components.StepsCard
import com.example.myapplication.viewmodel.MainViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chat.AIChatScreen
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.BottomNavigationBar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit,
    navController: NavController
) {
    var currentRoute by remember { mutableStateOf("home") }
    var moodValue by remember { mutableStateOf(0f) }
    var checkedStates by remember { mutableStateOf(listOf(true, false, false, true)) }
    var showChat by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 2.dp),
                    onClick = { showChat = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = "Emergency call"
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
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp )
                                .height(38.dp),
                            horizontalArrangement = SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.better_logo),
                                contentDescription = "logo",
                                modifier = Modifier.size(130.dp),
                                alignment = Alignment.CenterStart
                            )
                            IconButton(
                                onClick = {
                                    navController.navigate(Screen.Notifications.route) {
                                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_notif),
                                    contentDescription = "Notifications",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How are you feeling today?",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                    // Mood Selection with Slider
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = SpaceBetween
                        ) {
                            var moodValue by remember { mutableStateOf(0f) } // Start at 0

                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            ) {
                                Slider(
                                    value = moodValue,
                                    onValueChange = { moodValue = it },
                                    valueRange = 0f..2f,
                                    steps = 1,
                                    onValueChangeFinished = {
                                        moodValue = moodValue.roundToInt().toFloat()
                                    },
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.Transparent, // Hide default thumb
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                val icon = when (moodValue.roundToInt()) {
                                    0 -> Icons.Filled.SentimentDissatisfied // Sad 😞
                                    1 -> Icons.Filled.Face // Neutral 😐
                                    2 -> Icons.Filled.Mood // Happy 😊
                                    else -> Icons.Filled.Face
                                }

                                // Dynamically position the icon at the thumb location
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Mood Icon",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .offset(x = (moodValue / 2f) * (LocalConfiguration.current.screenWidthDp.dp - 94.dp))
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }


                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal=16.dp),
                            horizontalArrangement = SpaceBetween
                        ) {
                            Text(
                                text = "Awful",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (moodValue < 0.33f) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Neutral",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (moodValue in 0.33f..0.66f) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Good",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (moodValue > 0.66f) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 24.dp)
                            .fillMaxWidth()
                    )
                    // Health Summary Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Health Summary",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Edit",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.clickable { /* TODO: Handle edit */ }
                        )
                    }
                    // Steps Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        StepsCard(
                            steps = 2601,
                            goal = 5000
                        )
                    }
                    // Health Metrics Cards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = spacedBy(16.dp)
                    ) {
                        HealthMetricCard(
                            title = "HR",
                            value = "69",
                            unit = "BPM",
                            subtitle = "Latest",
                            icon = R.drawable.ic_heart,
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = spacedBy(16.dp)
                    ) {
                        HealthMetricCard(
                            title = "Water",
                            value = "500",
                            unit = "mL",
                            subtitle = "Latest",
                            icon = R.drawable.ic_water,
                            modifier = Modifier.weight(1f)
                        )
                        HealthMetricCard(
                            title = "Sleep",
                            value = "50",
                            unit = "MS",
                            subtitle = "Latest",
                            icon = R.drawable.ic_sleep,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = spacedBy(16.dp)
                    ) {
                        HealthMetricCard(
                            title = "Water",
                            value = "500",
                            unit = "mL",
                            subtitle = "Latest",
                            icon = R.drawable.ic_heart,
                            modifier = Modifier.weight(1f)
                        )

                    }
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .clickable(onClick = {
                                navController.navigate(Screen.Fall.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                                }
                            }),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Fall Detection",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                Text(
                                    text = "Enable tracking your falls for help",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(22.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 28.dp, bottom = 24.dp)
                            .fillMaxWidth()
                    )
                    // Activity Log with checkable items and strike-through
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Activity Log",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        TextButton(
                            onClick = { navController.navigate("activities") },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("View All")
                        }

                    }
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal= 20.dp)) {
                        // Vertical Blue Line
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(44.dp * (checkedStates.size))
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
                                        2 -> "Take Aspirin at 08:30 pm"
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
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 24.dp)
                            .fillMaxWidth()
                    )
                    // Recommendations
                    Text(
                        text = "Recommendations",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RecommendationCard(
                        title = "Try a 5-min walk to boost mood!",
                        subtitle = "Your last three mood logs were neutral."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RecommendationCard(
                        title = "Would you like to record vitals?",
                        subtitle = "You've missed recording vitals for 2 days."
                    )
                    Spacer(modifier = Modifier.height(98.dp))
                }
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
            modifier = Modifier.weight(1f)
                .then(if (isCompleted) Modifier.padding(start = 6.dp) else Modifier),
            color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.onSurface
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
