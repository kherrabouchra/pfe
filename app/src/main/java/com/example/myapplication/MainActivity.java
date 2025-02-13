package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer;

    // data thresholds
    private static final float FREE_FALL_THRESHOLD = 0.3f; // m/s² (near-zero gravity)
    private static final float IMPACT_THRESHOLD = 29.4f;   // 3g acceleration
    private static final float POST_IMPACT_STATIONARY_THRESHOLD = 1.5f; // m/s²
    private static final float ORIENTATION_CHANGE_THRESHOLD = 45f; // degrees
    private static final long FREE_FALL_DURATION = 300;    // ms
    private static final long IMPACT_WINDOW = 500;         // ms after free-fall
    private static final long STATIONARY_DURATION = 2000;  // ms post-impact

    private final Queue<Float> accelerationBuffer = new LinkedList<>();
    private static final int BUFFER_SIZE = 10;

    // State machine
    private enum FallState { NORMAL, FREE_FALL_DETECTED, IMPACT_DETECTED }
    private FallState currentState = FallState.NORMAL;

    private long freeFallStartTime = 0;
    private long impactTime = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] orientation = new float[3];
    private float initialPitch = 0;

    private TextView fallAlertTextView;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fallAlertTextView = findViewById(R.id.fallAlertTextView);
        okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> resetDetectionSystem());

        initializeSensors();
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                processAccelerometer(event);
                break;
            case Sensor.TYPE_GYROSCOPE:
                processGyroscope(event);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                break;
        }
    }

    private void processAccelerometer(SensorEvent event) {
        final float alpha = 0.8f; // Low-pass filter for gravity separation
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        float ax = event.values[0] - gravity[0];
        float ay = event.values[1] - gravity[1];
        float az = event.values[2] - gravity[2];
        float acceleration = (float) Math.sqrt(ax * ax + ay * ay + az * az);

        updateAccelerationBuffer(acceleration);
        detectFreeFall(acceleration);
        checkImpact(acceleration);
        checkPostImpactCondition();
    }

    private void processGyroscope(SensorEvent event) {
        // Not used in this example, could be implemented for future improvements
    }

    private void updateAccelerationBuffer(float value) {
        if (accelerationBuffer.size() >= BUFFER_SIZE) accelerationBuffer.poll();
        accelerationBuffer.add(value);
    }

    private void detectFreeFall(float acceleration) {
        if (currentState == FallState.NORMAL && acceleration < FREE_FALL_THRESHOLD) {
            if (freeFallStartTime == 0) freeFallStartTime = System.currentTimeMillis();

            if (System.currentTimeMillis() - freeFallStartTime > FREE_FALL_DURATION) {
                currentState = FallState.FREE_FALL_DETECTED;
                storeInitialOrientation();
                handler.postDelayed(this::cancelFreeFall, IMPACT_WINDOW);
            }
        } else {
            freeFallStartTime = 0;
        }
    }

    private void checkImpact(float acceleration) {
        if (currentState == FallState.FREE_FALL_DETECTED && acceleration >= IMPACT_THRESHOLD) {
            currentState = FallState.IMPACT_DETECTED;
            impactTime = System.currentTimeMillis();
            handler.removeCallbacks(this::cancelFreeFall);
        }
    }

    private void checkPostImpactCondition() {
        if (currentState == FallState.IMPACT_DETECTED) {
            boolean isStationary = calculateAccelerationVariance() < POST_IMPACT_STATIONARY_THRESHOLD;
            boolean orientationChanged = calculateOrientationChange();
            boolean inTimeWindow = (System.currentTimeMillis() - impactTime) < STATIONARY_DURATION;

            if (isStationary && orientationChanged && inTimeWindow) {
                confirmFall();
            } else if (!inTimeWindow) {
                resetDetectionSystem();
            }
        }
    }

    private float calculateAccelerationVariance() {
        float sum = 0, mean = 0, variance = 0;
        for (float val : accelerationBuffer) sum += val;
        mean = sum / accelerationBuffer.size();
        for (float val : accelerationBuffer) variance += (val - mean) * (val - mean);
        return variance / accelerationBuffer.size();
    }

    private void storeInitialOrientation() {
        if (SensorManager.getRotationMatrix(new float[9], null, gravity, geomagnetic)) {
            SensorManager.getOrientation(new float[9], orientation);
            initialPitch = (float) Math.abs(Math.toDegrees(orientation[1]));
        }
    }

    private boolean calculateOrientationChange() {
        if (SensorManager.getRotationMatrix(new float[9], null, gravity, geomagnetic)) {
            SensorManager.getOrientation(new float[9], orientation);
            float currentPitch = (float) Math.abs(Math.toDegrees(orientation[1]));
            return Math.abs(currentPitch - initialPitch) > ORIENTATION_CHANGE_THRESHOLD;
        }
        return false;
    }

    private void confirmFall() {
        runOnUiThread(() -> {
            fallAlertTextView.setText("Fall Detected!\nSeek assistance immediately!");
        });
        resetDetectionSystem();
    }

    private void cancelFreeFall() {
        if (currentState == FallState.FREE_FALL_DETECTED) {
            resetDetectionSystem();
        }
    }

    private void resetDetectionSystem() {
        currentState = FallState.NORMAL;
        freeFallStartTime = 0;
        impactTime = 0;
        accelerationBuffer.clear();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }
}
