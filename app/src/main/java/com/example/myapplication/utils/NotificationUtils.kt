package com.example.myapplication.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R

object NotificationUtils {
    fun createForegroundNotification(context: Context): Notification {
        val channelId = "fall_channel"
        createNotificationChannel(context, channelId)

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Fall Detection Active")
            .setSmallIcon(R.drawable.logochar  )
            .setOngoing(true)
            .build()
    }

    fun sendFallNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("fall_detected", true)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, "fall_channel")
            .setSmallIcon(R.drawable.logochar)
            .setContentTitle("Fall Detected")
            .setContentText("Tap to confirm you are okay.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, notification)
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Fall Detection",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
