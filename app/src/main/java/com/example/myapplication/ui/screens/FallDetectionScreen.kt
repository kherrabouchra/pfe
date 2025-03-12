package com.example.myapplication.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.viewmodel.FallDetectionViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FallDetectionScreen(
    viewModel: FallDetectionViewModel,
    onResetDetection: () -> Unit,
    navController: NavController
) {
    val fallAlertText by viewModel.fallAlertText.collectAsState()
    val isDetectionEnabled by viewModel.isDetectionEnabled.collectAsState()
    val showConfirmationDialog by viewModel.showConfirmationDialog.collectAsState()
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()
    val isCountdownActive by viewModel.isCountdownActive.collectAsState()


        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .blur(if (showConfirmationDialog) 8.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.Start),
                    onClick = { navController.navigate("Dashboard") }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.requiredSize(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.width(250.dp),
                        painter = painterResource(id = R.drawable.epilepsy_rafiki),
                        contentDescription = "Fall detection illustration",
                        alignment = Alignment.Center
                    )
                    
                    Text(
                        text = "Fall Detection",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                    )
                    
                    Text(
                        text = "Automatically detect falls and alert emergency contacts",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = Color.Gray
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Enable Fall Detection",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Switch(
                            checked = isDetectionEnabled,
                            onCheckedChange = { viewModel.enableFallDetection(it) }
                        )
                    }
                }

                if (fallAlertText.isNotEmpty() && !showConfirmationDialog) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = fallAlertText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onResetDetection) {
                        Text("Dismiss")
                    }
                }
            }

            // Fall confirmation dialog
            if (showConfirmationDialog) {
                Dialog(
                    onDismissRequest = {},
                    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                        ) {
                            Text(
                                text = "Fall Detected",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                            )

                            
                            Text(
                                text = "Are you okay? If this was just a phone drop, please confirm below, or else Emergency services will be notified in:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray,

                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            if (isCountdownActive) {
                               Column(horizontalAlignment = Alignment.CenterHorizontally){
                                   Text(
                                       text = "$countdownSeconds",
                                       style = MaterialTheme.typography.headlineLarge,
                                       fontWeight = FontWeight.Bold,
                                       textAlign = TextAlign.Center,
                                       color = MaterialTheme.colorScheme.primary
                                   )

                               }

                                
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedButton (
                                    onClick = { viewModel.confirmPhoneFall() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("I'm Okay")
                                }
                                
                                Button(
                                    onClick = { viewModel.markAsEmergency() },
                                    colors = ButtonDefaults.buttonColors(                             )
                                ) {
                                    Text("I Need Help")
                                }
                            }
                        }
                    }
                }
            }
        }
    }


@Preview(showSystemUi = true)
@Composable
fun FallDetectionScreenPreview() {
    val navController = rememberNavController() // Use a mock NavController
    FallDetectionScreen(
        viewModel = FallDetectionViewModel(
            application = TODO()
        ),
        onResetDetection = TODO(),
        navController = navController
    ) // Pass the navController

}