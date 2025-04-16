package com.example.myapplication.ui.screens

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.MeasurementState
import com.example.myapplication.data.SignalQuality
import com.example.myapplication.ui.components.PpgWaveformGraph
import com.example.myapplication.utils.HeartRateImageAnalyzer
import com.example.myapplication.viewmodel.HeartRateViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors
 
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HeartRateMonitorScreen(
    navController: NavController,
    onMeasurementComplete: (Int) -> Unit,
    viewModel: HeartRateViewModel = viewModel()
) {
    // Camera permission state
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val hasCameraPermission = cameraPermissionState.status.isGranted
    
    // Get measurement state from ViewModel
    val measurementState by viewModel.measurementState.collectAsState()
    val heartRateResult by viewModel.heartRateResult.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val signalQuality by viewModel.signalQuality.collectAsState()
    val isFingerDetected by viewModel.isFingerDetected.collectAsState()
    val ppgSignalData by viewModel.ppgSignalData.collectAsState()
    val lastPulseTimestamp by viewModel.lastPulseTimestamp.collectAsState()
    val currentBpmEstimate by viewModel.currentBpmEstimate.collectAsState()
    
    // Context and lifecycle owner for camera
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // We don't need the pulsating animation anymore as we'll use the progress indicator instead
    
    // Effect to handle measurement completion
    LaunchedEffect(measurementState) {
        if (measurementState == MeasurementState.Complete && heartRateResult != null) {
            val heartRate = heartRateResult!!.heartRate
            onMeasurementComplete(heartRate)
            // Navigate to the result screen with heart rate parameter
            navController.navigate("heart_rate_result/$heartRate")
        }
    }
    
    // Request camera permission if not granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Heart Rate Monitor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status indicator
            StatusIndicator(measurementState, signalQuality, isFingerDetected)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Main measurement area
            MeasurementDisplay(
                measurementState = measurementState,
                hasCameraPermission = hasCameraPermission,
                isFingerDetected = isFingerDetected,
                progress = progress,
                currentBpmEstimate = currentBpmEstimate,
                scale = 1f, // Fixed scale since we removed the pulsating animation
                context = context,
                lifecycleOwner = lifecycleOwner,
                viewModel = viewModel
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // PPG Waveform
            if (measurementState == MeasurementState.Measuring && isFingerDetected) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Pulse Waveform",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        PpgWaveformGraph(
                            ppgData = ppgSignalData,
                            lastPulseTimestamp = lastPulseTimestamp,
                            isFingerDetected = isFingerDetected,
                            modifier = Modifier.fillMaxSize(),
                            lineColor = MaterialTheme.colorScheme.primary,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Action buttons
            ActionButtons(measurementState, viewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Instructions
            InstructionsCard(measurementState, isFingerDetected)
        }
    }
}

@Composable
private fun StatusIndicator(
    measurementState: MeasurementState,
    signalQuality: SignalQuality,
    isFingerDetected: Boolean
) {
    val statusText = when (measurementState) {
        MeasurementState.Idle -> "Ready to measure"
        MeasurementState.Preparing -> "Preparing camera..."
        MeasurementState.Measuring -> if (isFingerDetected) {
            when (signalQuality) {
                SignalQuality.GOOD -> "Good signal quality"
                SignalQuality.POOR -> "Poor signal quality - hold still"
                SignalQuality.TOO_DARK -> "Signal too dark - adjust finger"
                SignalQuality.TOO_BRIGHT -> "Signal too bright - adjust finger"
                SignalQuality.UNKNOWN -> "Detecting signal..."
            }
        } else "Place finger on camera"
        MeasurementState.Processing -> "Processing results..."
        MeasurementState.Complete -> "Measurement complete"
        MeasurementState.Error -> "Error measuring heart rate"
    }
    
    val statusColor = when {
        measurementState == MeasurementState.Error -> Color(0xFFF44336) // Red
        measurementState == MeasurementState.Complete -> Color(0xFF4CAF50) // Green
        isFingerDetected && signalQuality == SignalQuality.GOOD -> Color(0xFF4CAF50) // Green
        isFingerDetected && signalQuality == SignalQuality.POOR -> Color(0xFFFFA726) // Orange
        else -> Color(0xFF2196F3) // Blue
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = statusColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = statusText,
                color = statusColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MeasurementDisplay(
    measurementState: MeasurementState,
    hasCameraPermission: Boolean,
    isFingerDetected: Boolean,
    progress: Int,
    currentBpmEstimate: Int,
    scale: Float, // Parameter kept for compatibility but not used anymore
    context: android.content.Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    viewModel: HeartRateViewModel
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        // Camera preview or heart icon based on measurement state
        if (hasCameraPermission && (measurementState == MeasurementState.Measuring || measurementState == MeasurementState.Preparing)) {
            // Camera preview for measurement
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Progress indicator around the camera circle
                CircularProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .size(220.dp)
                        .padding(4.dp),
                    strokeWidth = 4.dp,
                    trackColor = Color.Gray.copy(alpha = 0.3f),
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Recording indicator in the corner
                if (isFingerDetected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                }
                
                // Static border
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 4.dp,
                            color = if (isFingerDetected) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = CircleShape
                        )
                )

                // Camera preview
                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context).apply {
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                        
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build()
                            preview.setSurfaceProvider(previewView.surfaceProvider)
                            
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            
                            val analyzer = HeartRateImageAnalyzer(viewModel)
                            
                            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
                            
                            try {
                                // Unbind all use cases before rebinding
                                cameraProvider.unbindAll()
                                
                                // Bind use cases to camera - specify we want to use the back camera
                                val camera = cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalysis
                                )
                                
                                // Enable flash for better PPG signal
                                analyzer.setCamera(camera)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(context))
                        
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Show BPM estimate overlay if finger is detected and we have a valid estimate
                if (isFingerDetected && currentBpmEstimate > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$currentBpmEstimate",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "BPM",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Heart icon when not measuring
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Heart Rate",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                // Show error or completion message
                if (measurementState == MeasurementState.Error || measurementState == MeasurementState.Complete) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (measurementState == MeasurementState.Error) "Measurement Failed" 
                                  else "Measurement Complete",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    measurementState: MeasurementState,
    viewModel: HeartRateViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when (measurementState) {
            MeasurementState.Idle -> {
                Button(
                    onClick = { viewModel.startMeasurement() },
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Start Measurement")
                }
            }
            MeasurementState.Measuring, MeasurementState.Preparing -> {
                Button(
                    onClick = { viewModel.cancelMeasurement() },
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel")
                }
            }
            MeasurementState.Error -> {
                Button(
                    onClick = { viewModel.resetMeasurement() },
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Try Again")
                }
            }
            MeasurementState.Complete -> {
                Button(
                    onClick = { viewModel.resetMeasurement() },
                    modifier = Modifier.width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("New Measurement")
                }
            }
            else -> { /* No buttons for other states */ }
        }
    }
}

@Composable
private fun InstructionsCard(measurementState: MeasurementState, isFingerDetected: Boolean) {
    AnimatedVisibility(
        visible = measurementState == MeasurementState.Measuring && !isFingerDetected,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How to Measure",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("1. Place your fingertip gently over the camera lens")
                Text("2. Make sure the flash is covered by your finger")
                Text("3. Hold still until measurement completes")
                Text("4. Ensure your finger isn't pressing too hard or too lightly")
            }
        }
    }
}