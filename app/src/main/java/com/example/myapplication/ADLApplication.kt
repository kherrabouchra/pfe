package com.example.myapplication

import android.app.Application
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class ADLApplication : Application(), CameraXConfig.Provider {
    companion object {
        private const val TAG = "ADLApplication"
    }
    
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase with explicit initialization to ensure it's properly set up
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                // Initialize Firebase if not already initialized
                FirebaseApp.initializeApp(this)
                Log.d(TAG, "Firebase initialized successfully")
            } else {
                Log.d(TAG, "Firebase was already initialized")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase: ${e.message}")
        }
    }
    
    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}