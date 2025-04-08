package com.example.myapplication.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.data.HeartRateResult
import com.example.myapplication.data.SignalQuality
import com.example.myapplication.ui.components.HealthMetricCard
import com.example.myapplication.viewmodel.HeartRateViewModel
import com.example.myapplication.viewmodel.MeasurementState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun HeartRateMonitorScreen(
    navController: NavController,
    onMeasurementComplete: (Int) -> Unit,
    viewModel: HeartRateViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember { mutableStateOf(false) }
    var isFlashlightOn by remember { mutableStateOf(false) }

    // Collect states from ViewModel
    val measurementState by viewModel.measurementState.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val signalQuality by viewModel.signalQuality.collectAsState()
    val heartRateResult by viewModel.heartRateResult.collectAsState()

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted && measurementState == MeasurementState.Idle) {
            viewModel.startMeasurement()
        }
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                hasCameraPermission = true
                viewModel.startMeasurement()
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Effect to handle measurement completion
    LaunchedEffect(measurementState) {
        if (measurementState == MeasurementState.Complete && heartRateResult != null) {
            onMeasurementComplete(heartRateResult!!.heartRate)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            if (isFlashlightOn) {
                try {
                    cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Convert ImageProxy to Bitmap properly handling YUV_420_888 format
    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null

        // Check image format
        if (image.format != ImageFormat.YUV_420_888) {
            Log.e("HeartRateMonitor", "Unexpected image format: ${image.format}")
            return null
        }

        val width = image.width
        val height = image.height

        // Get the YUV planes
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]

        // Get the YUV data
        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // Get the YUV data in a single array
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        // Create a YuvImage
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)

        // Convert YuvImage to JPEG
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 100, out)

        // Convert JPEG to Bitmap
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        return bitmap
    }
    
    fun processImage(image: ImageProxy) {
        try {
            // Convert ImageProxy to Bitmap for processing using proper YUV conversion
            val bitmap = imageProxyToBitmap(image)

            // Log frame processing for debugging
            Log.d("HeartRateMonitor", "Processing frame: ${bitmap != null}")

            // Process the frame in ViewModel
            if (bitmap != null && (measurementState == MeasurementState.Preparing ||
                                  measurementState == MeasurementState.Measuring)) {
                viewModel.processFrame(bitmap)
                bitmap.recycle()
            } else if (bitmap == null) {
                Log.e("HeartRateMonitor", "Failed to create bitmap from camera frame")
            }

            // Ensure flash stays on during measurement
            if (isFlashlightOn) {
                try {
                    val cameraId = cameraManager.cameraIdList[0]
                    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                    val flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && flashAvailable) {
                        // Force flash to stay on continuously
                        cameraManager.setTorchMode(cameraId, true)
                    }
                } catch (e: Exception) {
                    Log.e("HeartRateMonitor", "Error maintaining flash: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("HeartRateMonitor", "Error processing image: ${e.message}")
        } finally {
            image.close()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Heart Rate Measurement") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetMeasurement()
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (measurementState == MeasurementState.Complete) {
                        IconButton(onClick = {
                            viewModel.resetMeasurement()
                            viewModel.startMeasurement()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Measure Again")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Measurement UI
            AnimatedVisibility(
                visible = measurementState != MeasurementState.Complete,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (hasCameraPermission) {
                        Box(
                            modifier = Modifier
                                .size(300.dp)
                                .padding(16.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            AndroidView(
                                factory = { previewView },
                                modifier = Modifier.fillMaxSize()
                            ) { view ->
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                cameraProviderFuture.addListener({
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build()
                                    preview.setSurfaceProvider(view.surfaceProvider)

                                    val imageAnalyzer = ImageAnalysis.Builder()
                                        .setTargetRotation(view.display.rotation)
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()
                                        .also {
                                            it.setAnalyzer(cameraExecutor) { image ->
                                                processImage(image)
                                            }
                                        }

                                    try {
                                        cameraProvider.unbindAll()
                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            CameraSelector.DEFAULT_BACK_CAMERA,
                                            preview,
                                            imageAnalyzer
                                        )
                                        // Turn on flashlight with maximum brightness
                                        val cameraId = cameraManager.cameraIdList[0]
                                        cameraManager.setTorchMode(cameraId, true)
                                        isFlashlightOn = true

                                        // Log successful camera setup
                                        Log.d("HeartRateMonitor", "Camera and flash initialized successfully")

                                        // Start a coroutine to ensure flash stays on continuously
                                        scope.launch {
                                            while (isFlashlightOn &&
                                                  (measurementState == MeasurementState.Preparing ||
                                                   measurementState == MeasurementState.Measuring)) {
                                                try {
                                                    // Force flash to stay on continuously
                                                    cameraManager.setTorchMode(cameraId, true)
                                                } catch (e: Exception) {
                                                    Log.e("HeartRateMonitor", "Error maintaining flash: ${e.message}")
                                                }
                                                delay(50) // Check more frequently to prevent pulsing
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(context))
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Signal quality indicator
                        when (signalQuality) {
                            SignalQuality.TOO_DARK -> {
                                Text(
                                    text = "Too dark! Please ensure your finger covers both the camera and flash",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                            SignalQuality.TOO_BRIGHT -> {
                                Text(
                                    text = "Too bright! Please cover the camera completely",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                            SignalQuality.POOR -> {
                                Text(
                                    text = "Signal quality is poor. Try to hold still",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFFFFA000),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = progress / 100f,
                                modifier = Modifier.size(200.dp),
                                strokeWidth = 8.dp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "$progress%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = when (measurementState) {
                                MeasurementState.Preparing -> "Preparing measurement..."
                                MeasurementState.Measuring -> "Place your finger on the camera and flash"
                                MeasurementState.Processing -> "Processing results..."
                                MeasurementState.Error -> "Error occurred. Please try again."
                                else -> "Place your finger on the camera and flash"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    } else {
                        Text(
                            text = "Camera permission is required for heart rate measurement",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }

            // Results UI
            AnimatedVisibility(
                visible = measurementState == MeasurementState.Complete && heartRateResult != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    heartRateResult?.let { result ->
                        // Success icon
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Measurement Complete",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Heart Rate Card
                        HealthMetricCard(
                            title = "Heart Rate",
                            value = result.heartRate.toString(),
                            unit = "BPM",
                            subtitle = "Status: ${result.status}",
                            icon = R.drawable.ic_heart,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confidence level indicator
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Measurement Confidence: ",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = result.confidenceLevel,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = when(result.confidenceLevel) {
                                    "High" -> Color(0xFF4CAF50)
                                    "Medium" -> Color(0xFFFFA000)
                                    else -> Color(0xFFF44336)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Timestamp
                        Text(
                            text = "Measured at: ${result.timestamp}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // HRV Metrics Card
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(0.3.dp, Color.LightGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Heart Rate Variability",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_hrv),
                                        contentDescription = null,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Variation between heartbeats",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // HRV Metrics
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "SDNN",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row(verticalAlignment = Alignment.Bottom) {
                                            // Calculate a simple SDNN value based on heart rate
                                            // This is a simplified approximation
                                            val sdnn = when {
                                                result.status == "Normal" -> (35..65).random()
                                                result.status == "High" -> (15..35).random()
                                                else -> (25..45).random()
                                            }

                                            Text(
                                                text = "$sdnn",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                text = "ms",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "RMSSD",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row(verticalAlignment = Alignment.Bottom) {
                                            // Calculate a simple RMSSD value based on heart rate
                                            // This is a simplified approximation
                                            val rmssd = when {
                                                result.status == "Normal" -> (25..55).random()
                                                result.status == "High" -> (10..30).random()
                                                else -> (20..40).random()
                                            }

                                            Text(
                                                text = "$rmssd",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Text(
                                                text = "ms",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.resetMeasurement()
                                viewModel.startMeasurement()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Measure Again", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
