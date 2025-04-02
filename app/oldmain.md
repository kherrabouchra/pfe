package com.example.stepcountercomp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stepcountercomp.ui.theme.StepCounterCompTheme
import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import com.example.stepcountercomp.ui.theme.CardBackground
import com.example.stepcountercomp.ui.theme.DarkBackground
import com.example.stepcountercomp.ui.theme.MoveRingColor
import com.example.stepcountercomp.ui.theme.StepCountColor
import com.example.stepcountercomp.ui.theme.DistanceColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.stepcountercomp.data.StepData
import com.example.stepcountercomp.ui.ChangeMoveGoalScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.Intent
import jdk.internal.javac.PreviewFeature

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("StepCounter", "Activity recognition permission granted")
            showMainUI()
        } else {
            Log.e("StepCounter", "Activity recognition permission denied")
            Toast.makeText(
                this,
                "Step counter requires activity recognition permission to work",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private var showChangeGoalScreen by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkAndRequestPermission()
        
        // Start the StepCounterService
        val serviceIntent = Intent(this, StepCounterService::class.java)
        startService(serviceIntent)
    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == 
                    PackageManager.PERMISSION_GRANTED -> {
                    Log.d("StepCounter", "Activity recognition permission already granted")
                    showMainUI()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION) -> {
                    Log.d("StepCounter", "Showing permission rationale")
                    setContent {
                        StepCounterCompTheme {
                            PermissionDialog(
                                onConfirm = {
                                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                                }
                            )
                        }
                    }
                }
                else -> {
                    Log.d("StepCounter", "Requesting activity recognition permission")
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        } else {
            showMainUI()
        }
    }

    private fun showMainUI() {
        setContent {
            StepCounterCompTheme {
                val viewModel: StepCounterViewModel = viewModel()
                if (showChangeGoalScreen) {
                    ChangeMoveGoalScreen(
                        currentGoal = viewModel.stepData.collectAsState().value.goal,
                        onGoalChange = { newGoal ->
                            viewModel.updateMoveGoal(newGoal)
                        },
                        onBack = { showChangeGoalScreen = false }
                    )
                } else {
                    StepCounterApp(viewModel, onChangeGoalClick = { showChangeGoalScreen = true })
                }
            }
        }
    }
}

@Composable
private fun PermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = { Text("The step counter needs activity recognition permission to track your steps") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Grant")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Deny")
            }
        }
    )
}

@Composable
fun StepCounterApp(viewModel: StepCounterViewModel, onChangeGoalClick: () -> Unit) {
    val stepData by viewModel.stepData.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = "Activity Summary",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Main Activity Ring
            ActivityCard(
                calories = stepData.calories,
                progress = stepData.caloriesProgressPercentage,
                onMoveGoalClick = onChangeGoalClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Step Count",
                    value = stepData.steps.toString(),
                    subtitle = "Today",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                
                StatCard(
                    title = "Distance",
                    value = String.format("%.2f", stepData.distance),
                    subtitle = "KM",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Sessions",
                    value = "0",
                    subtitle = "No sessions recorded",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                
                StatCard(
                    title = "Awards",
                    value = "0/100",
                    subtitle = "Move Goals",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(calories: Int, progress: Float, onMoveGoalClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Move Goal",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable { onMoveGoalClick() }
                )
                Text(
                    text = "$calories/110 kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Activity Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StepCounterAppPreview() {
    StepCounterCompTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main step counter display with circular progress
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(200.dp)
                ) {
                    CircularProgressIndicator(
                        progress = 0.45f,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 4.dp,
                        color = Color(0xFF4A67E9),
                        trackColor = Color(0xFFEEEEEE)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "45",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Steps",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(value = "123", label = "kcal")
                    StatItem(value = "1.5", label = "km")
                    StatItem(value = "30:00", label = "min")
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A67E9)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("START TRACKING")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4A67E9)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF4A67E9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("RESET")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatItemPreview() {
    StepCounterCompTheme {
        StatItem(
            value = "123",
            label = "kcal"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavItemPreview() {
    StepCounterCompTheme {
        Row {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                selected = true
            )
            BottomNavItem(
                icon = Icons.Default.Favorite,
                label = "Goals",
                selected = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StepDisplayPreview() {
    StepCounterCompTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            CircularProgressIndicator(
                progress = 0.45f,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 4.dp,
                color = Color(0xFF4A67E9),
                trackColor = Color(0xFFEEEEEE)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "45",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Steps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFF4A67E9) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) Color(0xFF4A67E9) else Color.Gray
        )
    }
}

@Preview
@Composable
private fun MainActivityPreview(
    MainActivity()
)