package com.example.myapplication.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.data.model.Medication
import com.example.myapplication.ui.components.HorizontalCalendar
import com.example.myapplication.ui.components.MedicationCard
import com.example.myapplication.ui.components.AddMedicationBottomSheet
import com.example.myapplication.viewmodel.MedicationViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderScreen(
    navController: NavController,
    medicationViewModel: MedicationViewModel = viewModel(),
    medicationId: String = ""
) {
    // State for selected date
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Get medication details if ID is provided
    val medications by medicationViewModel.medications.collectAsState()
    val medication = medications.find { it.id == medicationId } ?: Medication()
    
    // Format time to hh:mm
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    // State for showing edit confirmation dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // State for edit mode - moved to function level scope
    var isEditMode by remember { mutableStateOf(false) }
    var editedDosage by remember { mutableStateOf(medication.dosage) }
    var editedFrequency by remember { mutableStateOf(medication.frequency) }
    var editedInstructions by remember { mutableStateOf(medication.instructions) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            // Top app bar with back button and title
            Row(modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate("Activities") }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Reminder",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            
            // Main content
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                // Today text and medication name
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray
                )
                
                Text(
                    text = medication.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                // Action buttons (Complete, Edit, Remove)
                Row(modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    
                    // Complete button
                    Button(
                        onClick = { /* Mark as complete */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(alpha = 0.2f),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Complete",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Complete")
                        }
                    }
                    
                    // Edit button
                    Button(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(alpha = 0.2f),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit")
                        }
                    }
                    
                    // Remove button
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(alpha = 0.2f),
                            contentColor = Color.Red
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove")
                        }
                    }
                }
                
                // Time information
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Start time
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            medication.timeOfDay.firstOrNull()?.let { time ->
                                Text(
                                    text = time.format(timeFormatter),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    // End time (if applicable)
                    if (medication.timeOfDay.size > 1) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                medication.timeOfDay.getOrNull(1)?.let { time ->
                                    Text(
                                        text = time.format(timeFormatter),
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
                
                // Medication details
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Medication Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Dosage:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (isEditMode) {
                                TextField(
                                    value = editedDosage,
                                    onValueChange = { editedDosage = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                        unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                        disabledContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                    )
                                )
                            } else {
                                Text(
                                    text = medication.dosage,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Frequency:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (isEditMode) {
                                TextField(
                                    value = editedFrequency,
                                    onValueChange = { editedFrequency = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                        unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                        disabledContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                    )
                                )
                            } else {
                                Text(
                                    text = medication.frequency,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Instructions:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            if (isEditMode) {
                                TextField(
                                    value = editedInstructions,
                                    onValueChange = { editedInstructions = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                        unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                        disabledContainerColor = Color.LightGray.copy(alpha = 0.4f),
                                    )
                                )
                            } else {
                                Text(
                                    text = medication.instructions,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        
                        if (isEditMode) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Button(
                                    onClick = {
                                        // Save the edited medication
                                        val updatedMedication = medication.copy(
                                            dosage = editedDosage,
                                            frequency = editedFrequency,
                                            instructions = editedInstructions
                                        )
                                        medicationViewModel.updateMedication(updatedMedication)
                                        isEditMode = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Save")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { isEditMode = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Gray
                                    )
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
                
                // Calendar for date selection
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                HorizontalCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        selectedDate = date
                        // Here you would fetch medications for the selected date
                    }
                )
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    
    // Edit confirmation dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Medication") },
            text = { Text("Do you want to edit this medication?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        // Enable edit mode to transform fields into input fields
                        isEditMode = true
                    }
                ) {
                    Text("Edit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Medication") },
            text = { Text("Are you sure you want to delete this medication?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        // Only delete the specific medication by ID
                        medicationViewModel.deleteMedication(medication.id)
                        navController.navigateUp()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun ReminderScreenPreview() {
    val navController = rememberNavController()
    ReminderScreen(navController = navController)
}
