package com.example.myapplication;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer;
    private static final float FALL_THRESHOLD = 20.0f; // Acceleration threshold
    private static final float ROTATION_THRESHOLD = 3.0f; // Gyroscope threshold
    private boolean personFallDetected = false;
    private TextView fallAlertTextView; // TextView to display alert
    private Button okButton; // Button to reset fall alert

    private float[] gravity = new float[3]; // To store gravity data for low-pass filter
    private float[] geomagnetic = new float[3]; // To store magnetic field data
    private float[] rotationMatrix = new float[9]; // Rotation matrix
    private float[] orientation = new float[3]; // Orientation data (azimuth, pitch, roll)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextView to show fall alert
        fallAlertTextView = findViewById(R.id.fallAlertTextView);
        // Initialize OK button to reset the alert
        okButton = findViewById(R.id.okButton);

        // Set up OnClickListener for the OK button
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset the fall alert text
                fallAlertTextView.setText("No fall detected");
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("SensorInfo", "Sensor Type: " + event.sensor.getType());

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Low-pass filter to get gravity data
            final float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Calculate the acceleration magnitude
            float ax = event.values[0] - gravity[0];
            float ay = event.values[1] - gravity[1];
            float az = event.values[2] - gravity[2];
            float acceleration = (float) Math.sqrt(ax * ax + ay * ay + az * az);
            Log.d("SensorInfo", "Acceleration: " + acceleration);

            if (acceleration > FALL_THRESHOLD) {
                personFallDetected = true;
                Log.d("SensorInfo", "Fall detected with acceleration threshold");
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone();
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float rx = event.values[0];
            float ry = event.values[1];
            float rz = event.values[2];
            float rotation = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
            Log.d("SensorInfo", "Rotation: " + rotation);

            if (personFallDetected && rotation < ROTATION_THRESHOLD) {
                Log.d("FallDetection", "Rotation threshold met, checking orientation for more precision");

                // Use accelerometer and magnetometer data to compute orientation
                if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                    SensorManager.getOrientation(rotationMatrix, orientation);
                    float azimuth = orientation[0]; // azimuth
                    Log.d("SensorInfo", "Orientation Azimuth: " + azimuth);

                    // Depending on azimuth or other orientation parameters, refine fall detection logic
                    showFallAlert();
                    personFallDetected = false;
                }
            }
        }
    }

    // Method to display the fall alert on the screen
    private void showFallAlert() {
        fallAlertTextView.setText("A person may have fallen! Check immediately.");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
