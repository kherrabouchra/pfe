package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class FallDetectionService extends Service {

    private static final String CHANNEL_ID = "fall_alert";

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the notification channel if running on Android O or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Fall Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifies when a fall is detected");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a persistent notification to keep the service in the foreground
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Fall Detection Running")
                .setContentText("Monitoring for falls...")
                 .build();

        startForeground(1, notification);  // Start the service in the foreground

        // Your fall detection logic will go here. For now, we will simulate it.
        // After detecting a fall, show a notification.
        showFallNotification();

        // If the service is killed, restart it.
        return START_STICKY;
    }

    private void showFallNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Fall Detected")
                .setContentText("A person may have fallen! Check immediately.")
                 .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  // We don't bind this service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Handle cleanup if necessary
    }
}
