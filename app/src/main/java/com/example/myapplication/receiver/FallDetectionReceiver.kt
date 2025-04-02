package com.example.myapplication.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myapplication.MainActivity

/**
 * Broadcast receiver for fall detection events.
 * This receiver handles fall detection broadcasts from the FallDetectionService
 * and ensures the MainActivity is launched with the appropriate flags.
 */
class FallDetectionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "FallDetectionReceiver"
        const val ACTION_FALL_DETECTED = "com.example.myapplication.FALL_DETECTED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received broadcast: ${intent.action}")
        
        if (intent.action == ACTION_FALL_DETECTED) {
            Log.i(TAG, "Fall detection broadcast received, launching MainActivity")
            
            // Create an intent to launch MainActivity with fall confirmation
            val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("SHOW_FALL_CONFIRMATION", true)
            }
            
            // Start the MainActivity
            context.startActivity(mainActivityIntent)
        }
    }
}