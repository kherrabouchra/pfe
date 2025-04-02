// Initialize constants and thresholds
SCREEN_OFF_THRESHOLD = 30 minutes   // Duration of screen off to consider it "off"
MOVEMENT_THRESHOLD = 1.5 m/s²       // Minimum movement threshold to be considered significant
ROTATION_THRESHOLD = 0.5 rad/s      // Minimum rotation threshold to detect turning or waking
INACTIVITY_DURATION_THRESHOLD = 30 minutes // Duration of no movement to consider "inactivity"

// Function to detect if it is night time
function isNightTime(currentTime):
    return currentTime is between 10 PM and 6 AM  // Nighttime is between 10 PM and 6 AM

// Function to check if screen is off
function isScreenOff():
    return true if screen is off else false

// Function to check if there has been significant movement
function isMovementDetected(accelerometerData):
    motion = calculateMovement(accelerometerData)
    return motion > MOVEMENT_THRESHOLD

// Function to check if there has been significant rotation
function isRotationDetected(gyroscopeData):
    rotation = calculateRotation(gyroscopeData)
    return rotation > ROTATION_THRESHOLD

// Function to track user sleep status
function isUserSleeping(currentTime, accelerometerData, gyroscopeData, screenState):
    // Step 1: Check if it is night time
    if not isNightTime(currentTime):
        return false  // Not sleeping if it's not nighttime
    
    // Step 2: Check if the screen has been off for a prolonged period
    if not isScreenOff(screenState):
        return false  // User might be awake if screen is on

    // Step 3: Check if there has been no significant movement for 30 minutes
    if not isMovementDetected(accelerometerData):
        inactivityDuration = calculateInactivityDuration(lastMovementTime)
        if inactivityDuration < INACTIVITY_DURATION_THRESHOLD:
            return false  // User is still active (moving)
    
    // Step 4: Check if there has been no significant rotation for 30 minutes
    if not isRotationDetected(gyroscopeData):
        rotationDuration = calculateRotationDuration(lastRotationTime)
        if rotationDuration < INACTIVITY_DURATION_THRESHOLD:
            return false  // User is not rotating, but may be active in other ways

    // If all conditions are met, user is likely asleep
    return true

// Function to calculate movement from accelerometer data
function calculateMovement(accelerometerData):
    // Formula: motion = sqrt(accX^2 + accY^2 + accZ^2)
    motion = sqrt(accelerometerData.x^2 + accelerometerData.y^2 + accelerometerData.z^2)
    return motion

// Function to calculate rotation from gyroscope data
function calculateRotation(gyroscopeData):
    // Formula: rotation = sqrt(gyroX^2 + gyroY^2 + gyroZ^2)
    rotation = sqrt(gyroscopeData.x^2 + gyroscopeData.y^2 + gyroscopeData.z^2)
    return rotation

// Main Loop
while (true):
    currentTime = getCurrentTime()
    accelerometerData = getAccelerometerData()
    gyroscopeData = getGyroscopeData()
    screenState = getScreenState()
    
    if isUserSleeping(currentTime, accelerometerData, gyroscopeData, screenState):
        // Mark user as sleeping
        markUserAsSleeping()
    else:
        // User is awake
        markUserAsAwake()
    
    // Wait for the next sensor update (e.g., every 5 seconds)
    sleep(5 seconds)
