package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.components.BetterButton
import com.example.myapplication.ui.components.SelectButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionnaireScreen(
    navController: NavController,
    onComplete: () -> Unit = {}
) {
    var currentQuestion by remember { mutableStateOf(0) }
    val totalQuestions = 10 // Total number of questions
    
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
    
    // Mental & Emotional Well-being
    var moodState by remember { mutableStateOf("") }
    var isSociallyIsolated by remember { mutableStateOf<Boolean?>(null) }
    var wantsMotivationalPrompts by remember { mutableStateOf<Boolean?>(null) }
    
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
                    }
                )
                9 -> MentalEmotionalWellbeingQuestion(
                    moodState = moodState,
                    onMoodStateChange = { moodState = it },
                    isSociallyIsolated = isSociallyIsolated,
                    onIsSociallyIsolatedChange = { isSociallyIsolated = it },
                    wantsMotivationalPrompts = wantsMotivationalPrompts,
                    onWantsMotivationalPromptsChange = { wantsMotivationalPrompts = it }
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
            onValueChange = onDateOfBirthChange,
            label = { Text("Date of Birth (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth(),
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
            onValueChange = onHeightChange,
            label = { Text("Height (cm)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = { Text("Used for BMI, mobility assessment, and health monitoring") }
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
        
        OutlinedTextField(
            value = bedtime,
            onValueChange = onBedtimeChange,
            label = { Text("Usual bedtime") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = wakeupTime,
            onValueChange = onWakeupTimeChange,
            label = { Text("Usual wake-up time") },
            modifier = Modifier.fillMaxWidth()
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
    onHealthDevicesChange: (String, Boolean) -> Unit
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
    onIsSociallyIsolatedChange: (Boolean) -> Unit,
    wantsMotivationalPrompts: Boolean?,
    onWantsMotivationalPromptsChange: (Boolean) -> Unit
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Would you like motivational prompts from the app?", style = MaterialTheme.typography.bodyLarge)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            YesNoOption("Yes", wantsMotivationalPrompts == true) { onWantsMotivationalPromptsChange(true) }
            Spacer(modifier = Modifier.width(8.dp))
            YesNoOption("No", wantsMotivationalPrompts == false) { onWantsMotivationalPromptsChange(false) }
        }
    }
}

@Preview
@Composable
fun QuestionnaireScreenPreview() {
    val navController = rememberNavController()
    QuestionnaireScreen(navController = navController)
}