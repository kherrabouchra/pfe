package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserProfileEditForm(
    name: String,
    dateOfBirth: String,
    medicalConditions: String,
    allergies: String,
    bloodType: String,
    isWheelchairUser: Boolean,
    onNameChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onMedicalConditionsChange: (String) -> Unit,
    onAllergiesChange: (String) -> Unit,
    onBloodTypeChange: (String) -> Unit,
    onWheelchairChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.width(6.dp))

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Name Field
        Text(
            text = "Name",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        TextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.4f)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                disabledContainerColor = Color.LightGray.copy(alpha = 0.4f)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Date of Birth Field
        Text(
            text = "Date of Birth",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        TextField(
            value = dateOfBirth,
            onValueChange = onDateOfBirthChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.4f)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                disabledContainerColor = Color.LightGray.copy(alpha = 0.4f)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Medical Conditions Field
        Text(
            text = "Medical Conditions",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        TextField(
            value = medicalConditions,
            onValueChange = onMedicalConditionsChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.4f)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                disabledContainerColor = Color.LightGray.copy(alpha = 0.4f)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Allergies Field
        Text(
            text = "Allergies",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Parse existing allergies string into a list with a maximum limit
        val maxAllergies = 5 // Reduced maximum limit for better memory management
        val allergyList = remember(allergies) { 
            if (allergies.isBlank()) mutableStateListOf()
            else mutableStateListOf(*allergies.split(", ", "\n")
                .filter { it.isNotBlank() }
                .take(maxAllergies)
                .toTypedArray())
        }
        var currentAllergyInput by remember { mutableStateOf("") }
        
        // Function to update parent component with the current list of allergies
        val updateAllergies = {
            val allergiesText = allergyList.joinToString(", ")
            onAllergiesChange(allergiesText)
        }
        
        // Display existing allergy tags as chips with memory optimization
        if (allergyList.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 80.dp) // Reduced height
                    .padding(bottom = 8.dp)
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = 2 // Reduced items per row
                ) {
                    allergyList.forEachIndexed { index, allergy ->
                        key(allergy) { // Add key for better recomposition
                            SuggestionChip(
                                onClick = { },
                                label = { Text(allergy, maxLines = 1) }, // Limit text lines
                                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove $allergy",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable {
                                                allergyList.removeAt(index)
                                                updateAllergies()
                                            }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Input field for new allergies with memory optimization
        TextField(
            value = currentAllergyInput,
            onValueChange = { input ->
                // Limit input length for memory efficiency
                if (input.length <= 30) {
                    currentAllergyInput = input
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.4f))
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && currentAllergyInput.isNotBlank()) {
                        val trimmedInput = currentAllergyInput.trim()
                        if (allergyList.size < maxAllergies && !allergyList.contains(trimmedInput)) {
                            allergyList.add(trimmedInput)
                            updateAllergies()
                            currentAllergyInput = ""
                        }
                        true
                    } else {
                        false
                    }
                }
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && currentAllergyInput.isNotBlank()) {
                        val trimmedInput = currentAllergyInput.trim()
                        if (allergyList.size < maxAllergies && 
                            !allergyList.contains(trimmedInput)) {
                            allergyList.add(trimmedInput)
                            updateAllergies()
                            currentAllergyInput = ""
                        }
                        true
                    } else {
                        false
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                disabledContainerColor = Color.LightGray.copy(alpha = 0.4f)
            ),
            placeholder = { Text("Type allergy and press Enter") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Blood Type Field
        Text(
            text = "Blood Type",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // Blood type selection chips
        val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        
        Column(modifier = Modifier.fillMaxWidth()) {
            // First row: A+, A-, B+, B-
            Row(modifier = Modifier.fillMaxWidth()) {
                bloodTypes.take(4).forEach { type ->
                    SelectButton(
                        text = type,
                        isSelected = bloodType == type,
                        onSelect = { onBloodTypeChange(type) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = if (type != "B-") 4.dp else 0.dp)
                            .height(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Second row: AB+, AB-, O+, O-
            Row(modifier = Modifier.fillMaxWidth()) {
                bloodTypes.takeLast(4).forEach { type ->
                    SelectButton(
                        text = type,
                        isSelected = bloodType == type,
                        onSelect = { onBloodTypeChange(type) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = if (type != "O-") 4.dp else 0.dp)
                            .height(48.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Wheelchair Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Wheelchair",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Switch(
                checked = isWheelchairUser,
                onCheckedChange = onWheelchairChange
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onCancel
            ) {
                Text("Cancel")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save")
            }
        }


    }
    Spacer(modifier = Modifier.width(200.dp))
}