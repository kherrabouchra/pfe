package com.example.myapplication.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.ColorFilter
import com.example.myapplication.R
import com.example.myapplication.ui.components.HealthMetricCard
import com.example.myapplication.ui.components.RecommendationCard
import com.example.myapplication.ui.components.StepsCard
import com.example.myapplication.viewmodel.MainViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.base.Default
import com.example.chat.AIChatScreen
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.WaterIntakeCard
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit,
    navController: NavController
) {
    var currentRoute by remember { mutableStateOf("home") }
    var moodValue by remember { mutableStateOf(1f) }
    var checkedStates by remember { mutableStateOf(listOf(true, false, false, true)) }
    var showChat by remember { mutableStateOf(false) }
    var waterIntake by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize().background(color = Color.LightGray.copy(alpha = 0.3f)),
            floatingActionButton = {
                val context = LocalContext.current
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = 2.dp),
                    onClick = { makeEmergencyCall(context) },
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
                modifier = Modifier.background(color = Color.LightGray.copy(alpha = 0.1f))
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(160.dp)
                        .background(color = MaterialTheme.colorScheme.primary)
                ){
                    Column {

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp )
                                .height(70.dp),
                            horizontalArrangement = SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row (horizontalArrangement = Arrangement.Absolute.Left,
                                verticalAlignment = Alignment.CenterVertically){

                                Image(
                                    painter = painterResource(id = R.drawable.logo_simple),
                                    contentDescription = "logo",
                                    modifier = Modifier.size(190.dp).weight(2f),
                                    colorFilter = ColorFilter.tint(Color.White) ,

                                    )
                                Column (horizontalAlignment = Alignment.Start,
                                    modifier =Modifier.weight(5f) ){
                                    Text(
                                        text = "Hello,",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(  horizontal = 12.dp),
                                        fontWeight = FontWeight.Medium,
                                        color=Color.White

                                    )
                                    Text(
                                        text = "Johnny" +"!",
                                        style = MaterialTheme.typography.headlineLarge,
                                        modifier = Modifier.padding(  horizontal = 12.dp),
                                        fontWeight = FontWeight.Bold,
                                        color=Color.White

                                    )


                                }
                                Box(
                                    contentAlignment = Alignment.TopEnd,
                                    modifier =Modifier.weight(1f)
                                ) {
/*
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
                                        modifier = Modifier.size(32.dp),
                                        tint = Color.White
                                    )
                                }


                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White,
                                    modifier = Modifier.offset(x = (-6).dp, y = (6).dp)
                                ) {
                                    Text("4")
                                }*/
                                }

                            }



                        }
                    }

                }

                //HorizontalDivider(
                  //  modifier = Modifier
                   //     .padding(top = 12.dp, bottom = 16.dp)
                   //     .fillMaxWidth()
               // )
                Column(modifier = Modifier.padding(14.dp).offset(y = (-66).dp)) {



                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 0.dp),
                        horizontalArrangement = spacedBy(16.dp)
                    ) {
                        HealthMetricCard(
                            title = "Health Score",
                            value = "Good",
                            unit = " ",
                            subtitle = "Last 30 days",
                            icon = 0,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate(Screen.Vitals.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            }
                        )
                        HealthMetricCard(
                            title = "Symptoms",
                            value = "None",
                            unit = " ",
                            subtitle = "Latest\n ",
                            icon = 0,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate(Screen.Symptoms.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "How are you feeling today?",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(  horizontal = 14.dp),
                        fontWeight = FontWeight.Bold
                    )
                    // Mood Selection with Slider
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = SpaceBetween
                        ) {
                            var moodValue by remember { mutableStateOf(1f) } // Start at 0

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
                                        inactiveTrackColor = Color.LightGray.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                val icon = when (moodValue.roundToInt()) {
                                    0 -> Icons.Filled.SentimentDissatisfied
                                    1 -> Icons.Filled.Face
                                    2 -> Icons.Filled.Mood
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Awful",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (moodValue.roundToInt() == 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Neutral",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (moodValue.roundToInt() == 1) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Good",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (moodValue.roundToInt() == 2) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        StepsCard(
                            steps = 2601,
                            goal = 5000,
                            onClick = {
                                navController.navigate(Screen.StepCounter.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            })
                    }
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
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate("heart_rate_monitor") {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            }
                        )
                        HealthMetricCard(
                            title = "HRV",
                            value = "50",
                            unit = "MS",
                            subtitle = "Latest",
                            icon = R.drawable.ic_hrv,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate(Screen.Vitals.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            }
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
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate(Screen.Water.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            }
                        )
                        HealthMetricCard(
                            title = "Sleep",
                            value = "50",
                            unit = "MS",
                            subtitle = "Latest",
                            icon = R.drawable.sleep,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate(Screen.Sleep.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            }
                        )
                    }

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        WaterIntakeCard(
                            currentIntake = waterIntake,
                            targetIntake = 2500,
                            onIntakeChange = { waterIntake = it },
                            onClick = {
                                navController.navigate(Screen.Water.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            })
                    }


                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                            .padding(top = 28.dp, bottom = 10.dp)
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

                        Row (verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text = "Activity Log",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            Box(
                                modifier = Modifier.padding(horizontal=6.dp)
                                    .size(18.dp)
                                    .background(MaterialTheme.colorScheme.primary, 
                                        shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = checkedStates.size.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,

                                )
                            }
                        }

                        TextButton(
                            onClick = { navController.navigate("activities") },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = Color.Transparent
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
                    Column {


                    }
                    RecommendationCard(
                        title = "Try a 5-min walk to boost mood!",
                        subtitle = "Your last three mood logs were neutral.",
                        onClick = {
                            navController.navigate(Screen.Activities.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = false }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RecommendationCard(
                        title = "Would you like to record vitals?",
                        subtitle = "You've missed recording vitals for 2 days.",
                        onClick = {
                            navController.navigate(Screen.Vitals.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = false }
                            }
                        }
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

// Function to make emergency call
private fun makeEmergencyCall(context: Context) {
    val emergencyPhoneNumber = "123456789" // Using the same number as in VoiceCommandService
    
    // Check for phone call permission
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) 
            == PackageManager.PERMISSION_GRANTED) {
        try {
            // Create intent to make a phone call
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$emergencyPhoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(callIntent)
            
            // Show toast notification
            Toast.makeText(context, "Emergency call initiated", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Log.e("DashboardScreen", "Failed to initiate emergency call", e)
            Toast.makeText(context, "Failed to make emergency call", Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.e("DashboardScreen", "Cannot make emergency call - CALL_PHONE permission not granted")
        Toast.makeText(context, "Call permission not granted", Toast.LENGTH_SHORT).show()
    }
}

@Preview
@Composable
fun DashboardScreenPreview() {
     val navController = rememberNavController()
    DashboardScreen(viewModel = MainViewModel(), onSignOut = {}, navController = navController)
}
