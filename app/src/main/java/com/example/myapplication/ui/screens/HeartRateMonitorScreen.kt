package com.example.myapplication.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapplication.ui.components.PpgWaveformGraph
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.data.HeartRateResult
import com.example.myapplication.data.SignalQuality
import com.example.myapplication.utils.HeartRateImageAnalyzer
import com.example.myapplication.viewmodel.HeartRateViewModel
import com.example.myapplication.viewmodel.MeasurementState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.O)
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
    
    // Context and lifecycle owner for camera
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Animation for the heart beat
    val infiniteTransition = rememberInfiniteTransition(label = "heart_beat")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "heart_scale"
    )
    
    // Effect to handle measurement completion
    LaunchedEffect(measurementState) {
        if (measurementState == MeasurementState.Complete && heartRateResult != null) {
            onMeasurementComplete(heartRateResult!!.heartRate)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.size(30.dp)
                    )
                }
                
                Text(
                    text = "Heart Rate Monitor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Camera preview or heart icon based on measurement state
            if (hasCameraPermission && measurementState == MeasurementState.Measuring) {
                // Camera preview for measurement
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
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
                                
                                // Set up the preview use case
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                
                                // Set up image analysis use case
                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also {
                                        it.setAnalyzer(
                                            Executors.newSingleThreadExecutor(),
                                            HeartRateImageAnalyzer(viewModel)
                                        )
                                    }
                                
                                try {
                                    // Unbind all use cases before rebinding
                                    cameraProvider.unbindAll()
                                    
                                    // Select back camera and enable flash
                                    val cameraSelector = CameraSelector.Builder()
                                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                        .build()
                                    
                                    // Bind use cases to camera
                                    val camera = cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageAnalysis
                                    )
                                    
                                    // Enable flash
                                    camera.cameraControl.enableTorch(true)
                                    
                                } catch (e: Exception) {
                                    Log.e("HeartRateMonitor", "Camera binding failed", e)
                                }
                            }, ContextCompat.getMainExecutor(context))
                            
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Overlay with instructions
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Place your finger over the camera and flash",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                // Heart icon with animation when not measuring
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heart Rate",
                        modifier = Modifier
                            .size(80.dp)
                            .scale(if (measurementState == MeasurementState.Measuring) scale else 1f),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // PPG Waveform Graph - shows real-time pulse animation
            val ppgData by viewModel.ppgSignalData.collectAsState()
            val lastPulseTimestamp by viewModel.lastPulseTimestamp.collectAsState()
            
            PpgWaveformGraph(
                ppgData = ppgData,
                lastPulseTimestamp = lastPulseTimestamp,
                isFingerDetected = isFingerDetected,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 16.dp),
                lineColor = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Heart rate display
            Text(
                text = if (heartRateResult != null) "${heartRateResult!!.heartRate}" else "--",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "BPM",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status text (only show when measurement is complete)
            if (measurementState == MeasurementState.Complete && heartRateResult != null) {
                val statusColor = when (heartRateResult!!.status) {
                    "Low" -> Color(0xFFFFA000) // Amber for low
                    "High" -> Color.Red // Red for high
                    else -> Color.Green // Green for normal
                }
                
                val statusText = when (heartRateResult!!.status) {
                    "Low" -> "Low Heart Rate"
                    "High" -> "High Heart Rate"
                    else -> "Normal Heart Rate"
                }
                
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Normal range: 60-100 BPM",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Measured at: ${heartRateResult!!.timestamp}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Confidence: ${heartRateResult!!.confidenceLevel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Signal quality indicator (only during measurement)
            if (measurementState == MeasurementState.Measuring) {
                Spacer(modifier = Modifier.height(16.dp))
                
                val signalQualityText = when (signalQuality) {
                    SignalQuality.TOO_DARK -> "Too dark - please find better lighting"
                    SignalQuality.TOO_BRIGHT -> "Too bright - reduce lighting"
                    SignalQuality.POOR -> "Poor signal - adjust finger position"
                    SignalQuality.GOOD -> "Good signal - keep still"
                    else -> "Detecting signal..."
                }
                
                val signalQualityColor = when (signalQuality) {
                    SignalQuality.GOOD -> Color.Green
                    SignalQuality.POOR -> Color(0xFFFFA000) // Amber
                    SignalQuality.TOO_DARK, SignalQuality.TOO_BRIGHT -> Color.Red
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                }
                
                Text(
                    text = signalQualityText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = signalQualityColor,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (isFingerDetected) "Finger detected" else "No finger detected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isFingerDetected) Color.Green else Color.Red
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progress indicator (only show during measurement)
            if (measurementState == MeasurementState.Measuring || measurementState == MeasurementState.Preparing) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = when (measurementState) {
                        MeasurementState.Preparing -> "Preparing camera..."
                        MeasurementState.Measuring -> "Measuring... Please hold still"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Start/Restart button
            Button(
                onClick = { 
                    if (measurementState == MeasurementState.Idle || 
                        measurementState == MeasurementState.Complete || 
                        measurementState == MeasurementState.Error) {
                        viewModel.startMeasurement()
                    }
                },
                enabled = measurementState != MeasurementState.Measuring && 
                         measurementState != MeasurementState.Preparing && 
                         measurementState != MeasurementState.Processing,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = when {
                        measurementState == MeasurementState.Complete -> "Measure Again"
                        measurementState == MeasurementState.Error -> "Retry Measurement"
                        else -> "Start Measurement"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Instructions
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How to measure your heart rate",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "1. Place your fingertip gently over the camera lens and flash",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "2. Keep your finger still during the measurement",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "3. Ensure your fingertip covers both the camera and flash completely",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "4. Wait for the measurement to complete (about 15 seconds)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ComposePreview(showBackground = true)
@Composable
fun HeartRateMonitorScreenPreview() {
    val navController = rememberNavController()
    HeartRateMonitorScreen(
        navController = navController,
        onMeasurementComplete = {}
    )
}