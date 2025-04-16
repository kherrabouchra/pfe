package com.example.myapplication.utils

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.TorchState
import com.example.myapplication.viewmodel.HeartRateViewModel
import java.nio.ByteBuffer

/**
 * Image analyzer for heart rate detection using PPG (photoplethysmography) technique.
 * This analyzer processes camera frames to detect changes in blood volume in the fingertip
 * by analyzing the red channel intensity when a finger is placed over the camera with flash enabled.
 */
class HeartRateImageAnalyzer(private val viewModel: HeartRateViewModel) : ImageAnalysis.Analyzer {
    
    private val TAG = "HeartRateImageAnalyzer"
    
    // Frame processing parameters
    private val ROI_SIZE = 200 // Region of interest size in pixels
    
    // Reference to camera for controlling flash
    private var camera: Camera? = null
    
    // Flag to track if flash is enabled
    private var isFlashEnabled = false
    
    /**
     * Set camera reference to control flash
     */
    fun setCamera(camera: Camera) {
        this.camera = camera
        enableFlash()
    }
    
    /**
     * Enable camera flash (torch) for PPG measurement
     */
    fun enableFlash() {
        camera?.let { cam ->
            if (cam.cameraInfo.hasFlashUnit() && !isFlashEnabled) {
                cam.cameraControl.enableTorch(true)
                isFlashEnabled = true
                Log.d(TAG, "Flash enabled for PPG measurement")
            }
        }
    }
    
    /**
     * Disable camera flash when measurement is complete
     */
    fun disableFlash() {
        camera?.let { cam ->
            if (cam.cameraInfo.hasFlashUnit() && isFlashEnabled) {
                cam.cameraControl.enableTorch(false)
                isFlashEnabled = false
                Log.d(TAG, "Flash disabled after PPG measurement")
            }
        }
    }
    
    /**
     * Process each frame from the camera
     */
    override fun analyze(image: ImageProxy) {
        try {
            // Only process supported formats
            if (image.format == ImageFormat.YUV_420_888 || 
                image.format == ImageFormat.YUV_422_888 || 
                image.format == ImageFormat.YUV_444_888) {
                
                // Convert the image to bitmap for processing
                val bitmap = imageToBitmap(image)
                
                // Process the bitmap to extract PPG signal
                if (bitmap != null) {
                    viewModel.processFrame(bitmap)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing image", e)
        } finally {
            // Always close the image to avoid memory leaks
            image.close()
        }
    }
    
    /**
     * Convert ImageProxy to Bitmap for processing
     */
    private fun imageToBitmap(image: ImageProxy): Bitmap? {
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer
        
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        
        val nv21 = ByteArray(ySize + uSize + vSize)
        
        // U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        
        val yuvImage = android.graphics.YuvImage(
            nv21, 
            ImageFormat.NV21, 
            image.width, 
            image.height, 
            null
        )
        
        // Convert YUV to RGB
        val out = java.io.ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            android.graphics.Rect(0, 0, image.width, image.height),
            100, 
            out
        )
        
        val imageBytes = out.toByteArray()
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        
        // Extract the center region of interest (ROI) for better signal quality
        return extractCenterROI(bitmap)
    }
    
    /**
     * Extract the center region of interest from the bitmap for better signal quality
     */
    private fun extractCenterROI(bitmap: Bitmap): Bitmap? {
        try {
            val centerX = bitmap.width / 2
            val centerY = bitmap.height / 2
            
            // Calculate ROI boundaries ensuring they're within the bitmap dimensions
            val halfROI = ROI_SIZE / 2
            val left = (centerX - halfROI).coerceAtLeast(0)
            val top = (centerY - halfROI).coerceAtLeast(0)
            val right = (centerX + halfROI).coerceAtMost(bitmap.width)
            val bottom = (centerY + halfROI).coerceAtMost(bitmap.height)
            
            // Create a new bitmap with just the ROI
            return Bitmap.createBitmap(
                bitmap,
                left,
                top,
                right - left,
                bottom - top
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting ROI", e)
            return bitmap
        }
    }
}