package com.example.myapplication.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.math.*

fun detectPeaks(values: List<Float>): List<Int> {
    val peaks = mutableListOf<Int>()
    val windowSize = 10

    for (i in windowSize until values.size - windowSize) {
        val window = values.subList(i - windowSize, i + windowSize)
        if (values[i] == window.maxOrNull() && values[i] > window.average()) {
            peaks.add(i)
        }
    }

    return peaks
}
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun HeartRateMonitorScreen(
    navController: NavController,
    onMeasurementComplete: (Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasCameraPermission by remember { mutableStateOf(false) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var measurementProgress by remember { mutableStateOf(0f) }
    var isProcessing by remember { mutableStateOf(false) }
    var redValues = remember { mutableListOf<Float>() }
    var currentHeartRate by remember { mutableStateOf(0) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                hasCameraPermission = true
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
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

    fun processImage(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)

        // Calculate average red value
        val pixels = data.map { it.toInt() and 0xFF }
        val avgRed = pixels.average().toFloat()

        redValues.add(avgRed)

        // Update progress - reduced to 150 frames (5 seconds at 30fps)
        measurementProgress = (redValues.size / 150f).coerceIn(0f, 1f)

        // Ensure flash stays on during measurement
        if (isFlashlightOn) {
            try {
                // Instead of getTorchMode (which doesn't exist), check if flash is available and ensure it's on
                val cameraId = cameraManager.cameraIdList[0]
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && flashAvailable) {
                    cameraManager.setTorchMode(cameraId, true)
                }
            } catch (e: Exception) {
                Log.e("HeartRateMonitor", "Error maintaining flash: ${e.message}")
            }
        }

        if (redValues.size >= 150) { // 5 seconds of data at 30fps
            isProcessing = true
            // Calculate heart rate using peak detection
            val peaks = detectPeaks(redValues)
            val timeBetweenPeaks = 5f / peaks.size // 5 seconds divided by number of peaks
            currentHeartRate = (60f / timeBetweenPeaks).roundToInt()

            // Measurement complete
            scope.launch {
                onMeasurementComplete(currentHeartRate)
                navController.navigateUp()
            }
        }

        image.close()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Heart Rate Measurement") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
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
                                // Turn on flashlight and ensure it stays on
                                cameraManager.setTorchMode(cameraManager.cameraIdList[0], true)
                                isFlashlightOn = true
                                
                                // Start a coroutine to periodically check flash status
                                scope.launch {
                                    while (isFlashlightOn) {
                                        try {
                                            // Check if flash is available and ensure it's on
                                            val cameraId = cameraManager.cameraIdList[0]
                                            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                                            val flashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                                            
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && flashAvailable) {
                                                // We can't check the current torch state directly, so we'll just set it to on periodically
                                                cameraManager.setTorchMode(cameraId, true)
                                                Log.d("HeartRateMonitor", "Flash reactivated")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("HeartRateMonitor", "Error checking flash: ${e.message}")
                                        }
                                        delay(500) // Check every 500ms
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = measurementProgress,
                        modifier = Modifier.size(200.dp),
                        strokeWidth = 8.dp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "${(measurementProgress * 100).roundToInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Place your finger on the camera and flash",
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
}