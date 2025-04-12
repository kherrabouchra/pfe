package com.example.myapplication.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.components.BetterButton
import com.example.myapplication.ui.components.SelectButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.Calendar

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(8)
        val formatted = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i == 1 || i == 3) append('/')
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 1 -> offset
                    offset <= 3 -> offset + 1
                    offset <= 8 -> offset + 2
                    else -> 10
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    offset <= 10 -> offset - 2
                    else -> 8
                }
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
fun filterDateInput(input: String): String {
    return input.filter { it.isDigit() }.take(8)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
    navController: NavController,
    onComplete: () -> Unit = {}
) {
    var currentQuestion by remember { mutableStateOf(0) }
    val totalQuestions = 12 // Total number of questions (added smartwatch and caregiver pages)
    
    // Personal Information
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var hasDisability by remember { mutableStateOf<Boolean?>(null) }
    var disabilityDetails by remember { mutableStateOf("") }
    var usageGoal by remember { mutableStateOf("") }
    
    // Physical & Mobility Status
    var bathingStatus by remember { mutableStateOf("") }
    var dressingStatus by remember { mutableStateOf("") }
    var toiletingStatus by remember { mutableStateOf("") }
    var transferringStatus by remember { mutableStateOf("") }
    var eatingStatus by remember { mutableStateOf("") }
    var medicationStatus by remember { mutableStateOf("") }
    
    // Sleep & Rest Patterns
    var bedtime by remember { mutableStateOf("") }
    var wakeupTime by remember { mutableStateOf("") }
    var hasSleepDifficulty by remember { mutableStateOf<Boolean?>(null) }
    
    // Nutrition & Hydration
    var specialDiet by remember { mutableStateOf("") }
    var eatingFrequency by remember { mutableStateOf("") }
    var waterIntake by remember { mutableStateOf("") }
    
    // Health Conditions
    var hasChronicConditions by remember { mutableStateOf<Boolean?>(null) }
    var chronicConditionsDetails by remember { mutableStateOf("") }
    var fallRisk by remember { mutableStateOf<Boolean?>(null) }
    val healthDevices = remember { mutableStateListOf<String>() }
    var bloodType by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    
    // Mental & Emotional Well-being
    var moodState by remember { mutableStateOf("") }
    var isSociallyIsolated by remember { mutableStateOf<Boolean?>(null) }
    
    // Caregiver & Emergency Contacts
    var hasCaregiver by remember { mutableStateOf<Boolean?>(null) }
    var emergencyContactName by remember { mutableStateOf("") }
    var emergencyContactPhone by remember { mutableStateOf("") }
    var enableCaregiverNotifications by remember { mutableStateOf<Boolean?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assess Your Needs") },
                actions = {
                    TextButton(onClick = { /* Skip for now */ }) {
                        Text("Finish later", color = Color(0xFF2547CE))
                    }
                },
                navigationIcon = {
                    if (currentQuestion > 0) {
                        IconButton(onClick = { currentQuestion-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(22.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentQuestion + 1).toFloat() / totalQuestions },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Question counter
            Text(
                text = "Question ${currentQuestion + 1} of $totalQuestions",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            Column (modifier = Modifier.height(360.dp)
                .verticalScroll(rememberScrollState())){
            // Question content
            when (currentQuestion) {
                0 -> PersonalInfoQuestion(
                    fullName = fullName,
                    onFullNameChange = { fullName = it },
                    dateOfBirth = dateOfBirth,
                    onDateOfBirthChange = { dateOfBirth = it },
                    selectedGender = selectedGender,
                    onGenderSelect = { selectedGender = it }
                )
                1 -> PhysicalInfoQuestion(
                    height = height,
                    onHeightChange = { height = it },
                    weight = weight,
                    onWeightChange = { weight = it }
                )
                2 -> DisabilityQuestion(
                    hasDisability = hasDisability,
                    onHasDisabilityChange = { hasDisability = it },
                    disabilityDetails = disabilityDetails,
                    onDisabilityDetailsChange = { disabilityDetails = it }
                )
                3 -> UsageGoalQuestion(
                    selectedGoal = usageGoal,
                    onGoalSelect = { usageGoal = it }
                )
                4 -> ADLTrackingQuestion()
                5 -> PhysicalMobilityStatusQuestion(
                    bathingStatus = bathingStatus,
                    onBathingStatusChange = { bathingStatus = it },
                    dressingStatus = dressingStatus,
                    onDressingStatusChange = { dressingStatus = it },
                    toiletingStatus = toiletingStatus,
                    onToiletingStatusChange = { toiletingStatus = it },
                    transferringStatus = transferringStatus,
                    onTransferringStatusChange = { transferringStatus = it },
                    eatingStatus = eatingStatus,
                    onEatingStatusChange = { eatingStatus = it },
                    medicationStatus = medicationStatus,
                    onMedicationStatusChange = { medicationStatus = it }
                )
                6 -> SleepRestPatternsQuestion(
                    bedtime = bedtime,
                    onBedtimeChange = { bedtime = it },
                    wakeupTime = wakeupTime,
                    onWakeupTimeChange = { wakeupTime = it },
                    hasSleepDifficulty = hasSleepDifficulty,
                    onHasSleepDifficultyChange = { hasSleepDifficulty = it }
                )
                7 -> NutritionHydrationQuestion(
                    specialDiet = specialDiet,
                    onSpecialDietChange = { specialDiet = it },
                    eatingFrequency = eatingFrequency,
                    onEatingFrequencyChange = { eatingFrequency = it },
                    waterIntake = waterIntake,
                    onWaterIntakeChange = { waterIntake = it }
                )
                8 -> HealthConditionsQuestion(
                    hasChronicConditions = hasChronicConditions,
                    onHasChronicConditionsChange = { hasChronicConditions = it },
                    chronicConditionsDetails = chronicConditionsDetails,
                    onChronicConditionsDetailsChange = { chronicConditionsDetails = it },
                    fallRisk = fallRisk,
                    onFallRiskChange = { fallRisk = it },
                    healthDevices = healthDevices,
                    onHealthDevicesChange = { device, isSelected ->
                        if (isSelected) healthDevices.add(device)
                        else healthDevices.remove(device)
                    },
                    bloodType = bloodType,
                    onBloodTypeChange = { bloodType = it },
                    allergies = allergies,
                    onAllergiesChange = { allergies = it }
                )
                9 -> MentalEmotionalWellbeingQuestion(
                    moodState = moodState,
                    onMoodStateChange = { moodState = it },
                    isSociallyIsolated = isSociallyIsolated,
                    onIsSociallyIsolatedChange = { isSociallyIsolated = it }
                )
                10 -> SmartWatchConnectionQuestion(
                    healthDevices = healthDevices,
                    onHealthDevicesChange = { device, isSelected ->
                        if (isSelected) healthDevices.add(device)
                        else healthDevices.remove(device)
                    }
                )
                11 -> CaregiverEmergencyContactsQuestion(
                    hasCaregiver = hasCaregiver,
                    onHasCaregiverChange = { hasCaregiver = it },
                    emergencyContactName = emergencyContactName,
                    onEmergencyContactNameChange = { emergencyContactName = it },
                    emergencyContactPhone = emergencyContactPhone,
                    onEmergencyContactPhoneChange = { emergencyContactPhone = it },
                    enableCaregiverNotifications = enableCaregiverNotifications,
                    onEnableCaregiverNotificationsChange = { enableCaregiverNotifications = it }
                )
            }}
            
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    ,
                horizontalArrangement = Arrangement.Center
            ) {
                if (currentQuestion > 0) {
                  /*  BetterButton(
                        onClick = { currentQuestion-- },
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        text="Back",
                       isTransparent =true )*/

                } else {

                }
                BetterButton(
                    onClick = {
                        if (currentQuestion < totalQuestions - 1) {
                            currentQuestion++
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier.weight(1.6f),
                    text = (if (currentQuestion < totalQuestions - 1) "Next" else "Complete"))

            }

            }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoQuestion(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    dateOfBirth: String,
    onDateOfBirthChange: (String) -> Unit,
    selectedGender: String,
    onGenderSelect: (String) -> Unit
) {
    Column {
        Text(
            text = "👤Personal Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { newInput ->
                onDateOfBirthChange(filterDateInput(newInput))
            },
            label = { Text("Date of Birth (DD/MM/YYYY)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            visualTransformation = DateVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "Used to calculate age for vitals interpretation, sleep needs, and reminders",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Gender", style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            "Some health references differ by gender",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            GenderOption("Male", selectedGender == "Male", onGenderSelect)
            Spacer(modifier = Modifier.width(8.dp))
            GenderOption("Female", selectedGender == "Female", onGenderSelect)
        }
    }
}

@Composable
fun GenderOption(gender: String, isSelected: Boolean, onSelect: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) Color(0xFFE3E8FF) else Color.Transparent)
            .padding(8.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(gender) },
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2547CE))
        )
        Text(gender, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun PhysicalInfoQuestion(
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit
) {
    Column {
        Text(
            text = "💪Physical Information",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = height,
            onValueChange = { newInput ->
                onHeightChange(newInput.filter { it.isDigit() }) // Digits only
            },
            label = { Text("Height (cm)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { newInput ->
                onWeightChange(newInput.filter { it.isDigit() }) // Digits only
            },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text("Used for BMI, mobility assessment, and health monitoring")
            }
        )

    }
}

@Composable
fun DisabilityQuestion(
    hasDisability: Boolean?,
    onHasDisabilityChange: (Boolean) -> Unit,
    disabilityDetails: String,
    onDisabilityDetailsChange: (String) -> Unit
) {
    Column {
        Text(
            text = "🧑‍🦽Do you have any disabilities?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            YesNoOption("Yes", hasDisability == true) { onHasDisabilityChange(true) }
            Spacer(modifier = Modifier.width(8.dp))
            YesNoOption("No", hasDisability == false) { onHasDisabilityChange(false) }
        }
        
        if (hasDisability == true) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = disabilityDetails,
                onValueChange = onDisabilityDetailsChange,
                label = { Text("Please specify") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun YesNoOption(text: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) Color(0xFFE3E8FF) else Color.Transparent)
            .padding(8.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2547CE))
        )
        Text(text, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun UsageGoalQuestion(
    selectedGoal: String,
    onGoalSelect: (String) -> Unit
) {
    Column {
        Text(
            text = "🎯 Usage Goal",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Are you using this app for:",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SelectButton(
            text = "Short-term recovery",
            description = "Post-surgery, injury, temporary health condition",
            isSelected = selectedGoal == "short-term",
            onSelect = { onGoalSelect("short-term") },

        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        SelectButton(
            text = "Long-term assistance",
            description = "Aging, chronic conditions, independent living support",
            isSelected = selectedGoal == "long-term",
            onSelect = { onGoalSelect("long-term") }
        )
    }
}



@Composable
fun ADLTrackingQuestion() {
    var selectedADLs by remember { mutableStateOf(setOf<String>()) }
    
    Column {
        Text(
            text = "📌 ADLs You Wish to Track",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Select all that apply:",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val adlOptions = listOf(
            "bathing" to Pair("Bathing & Hygiene", "Track daily hygiene activities"),
            "dressing" to Pair("Dressing", "Monitor clothing and dressing assistance needs"),
            "medication" to Pair("Medication Management", "Track medication schedules and adherence"),
            "mobility" to Pair("Mobility & Transfers", "Monitor movement and transfer assistance"),
            "sleep" to Pair("Sleep & Rest Patterns", "Track sleep quality and patterns"),
            "nutrition" to Pair("Nutrition & Hydration", "Monitor eating habits and water intake"),
            "vitals" to Pair("Vitals & Symptom Monitoring", "Track health metrics and symptoms"),
            "mood" to Pair("Mood & Emotional Well-being", "Monitor emotional state and mental health")
        )
        
        // Using Column with Row pairs instead of LazyVerticalGrid for better rendering
        Column(modifier = Modifier.fillMaxWidth()) {
            for (i in adlOptions.indices step 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // First item in the row
                    val (key1, value1) = adlOptions[i]
                    SelectButton(
                        text = value1.first,
                        description = value1.second,
                        isSelected = selectedADLs.contains(key1),
                        onSelect = {
                            selectedADLs = if (selectedADLs.contains(key1)) {
                                selectedADLs - key1
                            } else {
                                selectedADLs + key1
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Second item in the row (if exists)
                    if (i + 1 < adlOptions.size) {
                        val (key2, value2) = adlOptions[i + 1]
                        SelectButton(
                            text = value2.first,
                            description = value2.second,
                            isSelected = selectedADLs.contains(key2),
                            onSelect = {
                                selectedADLs = if (selectedADLs.contains(key2)) {
                                    selectedADLs - key2
                                } else {
                                    selectedADLs + key2
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Empty space to maintain layout if odd number of items
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@Composable
fun CheckboxOption(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle),
        color = if (isSelected) Color(0xFFE3E8FF) else MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2547CE))
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
@Composable
fun TimeInputField(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit
) {
    val context = LocalContext.current
    val timeParts = time.split(":").mapNotNull { it.toIntOrNull() }
    val initialHour = timeParts.getOrNull(0) ?: 22
    val initialMinute = timeParts.getOrNull(1) ?: 0

    OutlinedTextField(
        value = time,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                android.app.TimePickerDialog(
                    context,
                    { _, hour: Int, minute: Int ->
                        val formattedTime = String.format("%02d:%02d", hour, minute)
                        onTimeChange(formattedTime)
                    },
                    initialHour,
                    initialMinute,
                    true
                ).show()
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun PhysicalMobilityStatusQuestion(
    bathingStatus: String,
    onBathingStatusChange: (String) -> Unit,
    dressingStatus: String,
    onDressingStatusChange: (String) -> Unit,
    toiletingStatus: String,
    onToiletingStatusChange: (String) -> Unit,
    transferringStatus: String,
    onTransferringStatusChange: (String) -> Unit,
    eatingStatus: String,
    onEatingStatusChange: (String) -> Unit,
    medicationStatus: String,
    onMedicationStatusChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "🧍 Physical & Mobility Status",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Group items in pairs for better layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectButton(
                text = "Bathing",
                description = "Ability to bathe independently",
                isSelected = bathingStatus == "Independent",
                onSelect = { 
                    // Toggle between Independent and empty string
                    if (bathingStatus == "Independent") {
                        onBathingStatusChange("") 
                    } else {
                        onBathingStatusChange("Independent")
                    }
                },
                modifier = Modifier.weight(1f)
            )
            
            SelectButton(
                text = "Dressing",
                description = "Ability to dress independently",
                isSelected = dressingStatus == "Independent",
                onSelect = { 
                    // Toggle between Independent and empty string
                    if (dressingStatus == "Independent") {
                        onDressingStatusChange("") 
                    } else {
                        onDressingStatusChange("Independent")
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectButton(
                text = "Toileting",
                description = "Ability to use bathroom independently",
                isSelected = toiletingStatus == "Independent",
                onSelect = { 
                    // Toggle between Independent and empty string
                    if (toiletingStatus == "Independent") {
                        onToiletingStatusChange("") 
                    } else {
                        onToiletingStatusChange("Independent")
                    }
                },
                modifier = Modifier.weight(1f)
            )
            
            SelectButton(
                text = "Transferring",
                description = "Ability to move between bed and chair",
                isSelected = transferringStatus == "Independent",
                onSelect = { 
                    // Toggle between Independent and empty string
                    if (transferringStatus == "Independent") {
                        onTransferringStatusChange("") 
                    } else {
                        onTransferringStatusChange("Independent")
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectButton(
                text = "Eating",
                description = "Ability to eat independently",
                isSelected = eatingStatus == "Independent",
                onSelect = { 
                    // Toggle between Independent and empty string
                    if (eatingStatus == "Independent") {
                        onEatingStatusChange("") 
                    } else {
                        onEatingStatusChange("Independent")
                    }
                },
                modifier = Modifier.weight(1f)
            )
            
            SelectButton(
                text = "Medication Management",
                description = "Ability to manage medications independently",
                isSelected = medicationStatus == "Independent",
                onSelect = { 
                    // Toggle between Independent and empty string
                    if (medicationStatus == "Independent") {
                        onMedicationStatusChange("") 
                    } else {
                        onMedicationStatusChange("Independent")
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ADLStatusItem(
    title: String,
    selectedStatus: String,
    onStatusChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            listOf("Independent", "Needs Some Help", "Fully Assisted").forEach { status ->
                ADLStatusOption(status, selectedStatus == status) { onStatusChange(status) }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun ADLStatusOption(text: String, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onSelect),
        color = if (isSelected) Color(0xFFE3E8FF) else MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2547CE))
            )
            Text(text, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
fun SleepRestPatternsQuestion(
    bedtime: String,
    onBedtimeChange: (String) -> Unit,
    wakeupTime: String,
    onWakeupTimeChange: (String) -> Unit,
    hasSleepDifficulty: Boolean?,
    onHasSleepDifficultyChange: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "😴 Sleep & Rest Patterns",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        TimeInputField(
            label = "Usual bedtime",
            time = bedtime,
            onTimeChange = onBedtimeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimeInputField(
            label = "Usual wake-up time",
            time = wakeupTime,
            onTimeChange = onWakeupTimeChange
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Do you experience sleep difficulty?", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            YesNoOption("Yes", hasSleepDifficulty == true) { onHasSleepDifficultyChange(true) }
            Spacer(modifier = Modifier.width(8.dp))
            YesNoOption("No", hasSleepDifficulty == false) { onHasSleepDifficultyChange(false) }
        }
    }
}

@Composable
fun NutritionHydrationQuestion(
    specialDiet: String,
    onSpecialDietChange: (String) -> Unit,
    eatingFrequency: String,
    onEatingFrequencyChange: (String) -> Unit,
    waterIntake: String,
    onWaterIntakeChange: (String) -> Unit
) {
    Column {
        Text(
            text = "🍽 Nutrition & Hydration",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Do you follow a special diet?", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("No", "Diabetic", "Low Sodium", "Other").forEach { diet ->
                ADLStatusOption(diet, specialDiet == diet) { onSpecialDietChange(diet) }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Do you typically eat all three meals a day?", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("Often", "Sometimes", "Rarely").forEach { frequency ->
                ADLStatusOption(frequency, eatingFrequency == frequency) { onEatingFrequencyChange(frequency) }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Do you drink enough water daily?", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("Often", "Sometimes", "Rarely").forEach { intake ->
                ADLStatusOption(intake, waterIntake == intake) { onWaterIntakeChange(intake) }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun HealthConditionsQuestion(
    hasChronicConditions: Boolean?,
    onHasChronicConditionsChange: (Boolean) -> Unit,
    chronicConditionsDetails: String,
    onChronicConditionsDetailsChange: (String) -> Unit,
    fallRisk: Boolean?,
    onFallRiskChange: (Boolean) -> Unit,
    healthDevices: List<String>,
    onHealthDevicesChange: (String, Boolean) -> Unit,
    bloodType: String,
    onBloodTypeChange: (String) -> Unit,
    allergies: String,
    onAllergiesChange: (String) -> Unit
) {
    Column {
        Text(
            text = "❤️ Health Conditions",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Do you have any chronic conditions?", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            YesNoOption("Yes", hasChronicConditions == true) { onHasChronicConditionsChange(true) }
            Spacer(modifier = Modifier.width(8.dp))
            YesNoOption("No", hasChronicConditions == false) { onHasChronicConditionsChange(false) }
        }
        
        if (hasChronicConditions == true) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = chronicConditionsDetails,
                onValueChange = onChronicConditionsDetailsChange,
                label = { Text("Please specify") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Are you at risk of falling?", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            YesNoOption("Yes", fallRisk == true) { onFallRiskChange(true) }
            Spacer(modifier = Modifier.width(8.dp))
            YesNoOption("No", fallRisk == false) { onFallRiskChange(false) }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Blood Type", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-").forEach { type ->
                ADLStatusOption(type, bloodType == type) { onBloodTypeChange(type) }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = allergies,
            onValueChange = onAllergiesChange,
            label = { Text("Allergies (if any)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Do you use any of the following devices?", style = MaterialTheme.typography.bodyLarge)
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("Smartwatch", "Blood Pressure Monitor", "Glucose Monitor", "Other").forEach { device ->
                CheckboxOption(
                    text = device,
                    isSelected = healthDevices.contains(device),
                    onToggle = { onHealthDevicesChange(device, !healthDevices.contains(device)) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun MentalEmotionalWellbeingQuestion(
    moodState: String,
    onMoodStateChange: (String) -> Unit,
    isSociallyIsolated: Boolean?,
    onIsSociallyIsolatedChange: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "😊 Mental & Emotional Well-being",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Your typical mood state:", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("Calm", "Anxious", "Low Energy", "High Energy", "Other").forEach { mood ->
                ADLStatusOption(mood, moodState == mood) { onMoodStateChange(mood) }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Do you feel socially isolated?", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            YesNoOption("Yes", isSociallyIsolated == true) { onIsSociallyIsolatedChange(true) }
            Spacer(modifier = Modifier.width(8.dp))
            YesNoOption("No", isSociallyIsolated == false) { onIsSociallyIsolatedChange(false) }
        }
    }
}

@Composable
fun SmartWatchConnectionQuestion(
    healthDevices: List<String>,
    onHealthDevicesChange: (String, Boolean) -> Unit
) {
    Column {
        Text(
            text = "⌚ Smartwatch Connection",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        

        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Connect a smartwatch to enhance health monitoring",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            modifier = Modifier.width(180.dp),
            painter = painterResource(id = R.drawable.epilepsy_rafiki),
            contentDescription = "Fall detection illustration",
            alignment = Alignment.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Do you have any of these smartwatch devices?", style = MaterialTheme.typography.bodyLarge)
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("Apple Watch", "Samsung Galaxy Watch", "Fitbit", "Garmin", "Other").forEach { device ->
                CheckboxOption(
                    text = device,
                    isSelected = healthDevices.contains(device),
                    onToggle = { onHealthDevicesChange(device, !healthDevices.contains(device)) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "If you don't have a smartwatch yet, you can still use the app and connect one later.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun CaregiverEmergencyContactsQuestion(
    hasCaregiver: Boolean?,
    onHasCaregiverChange: (Boolean) -> Unit,
    emergencyContactName: String,
    onEmergencyContactNameChange: (String) -> Unit,
    emergencyContactPhone: String,
    onEmergencyContactPhoneChange: (String) -> Unit,
    enableCaregiverNotifications: Boolean?,
    onEnableCaregiverNotificationsChange: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "👨‍⚕️ Emergency Contacts",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        

        Spacer(modifier = Modifier.height(16.dp))
        
         Text(
            "Enter your emergency contact's informations. This person will be contacted in case of emergency",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = emergencyContactName,
            onValueChange = onEmergencyContactNameChange,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = emergencyContactPhone,
            onValueChange = onEmergencyContactPhoneChange,
            label = { Text("Phone") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (hasCaregiver == true) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Enable caregiver notifications?", style = MaterialTheme.typography.bodyLarge)
            Text(
                "Allow your caregiver to receive alerts about your health status",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                YesNoOption("Yes", enableCaregiverNotifications == true) { onEnableCaregiverNotificationsChange(true) }
                Spacer(modifier = Modifier.width(8.dp))
                YesNoOption("No", enableCaregiverNotifications == false) { onEnableCaregiverNotificationsChange(false) }
            }
        }
    }
}

@Preview
@Composable
fun QuestionnaireScreenPreview() {
    val navController = rememberNavController()
    QuestionnaireScreen(navController = navController)
}