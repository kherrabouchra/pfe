package com.example.myapplication.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
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
import com.example.myapplication.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationBottomSheet(
    onDismiss: () -> Unit
) {
    var medicationName by remember { mutableStateOf("") }
    var selectedMedicineType by remember { mutableIntStateOf(0) } // 0: Pill, 1: Capsule, 2: Syringe, 3: Drops
    var quantity by remember { mutableIntStateOf(1) }
    var timeSchedule by remember { mutableStateOf("08.00 AM") }
    var duration by remember { mutableStateOf("10 Days") }
    var beforeFood by remember { mutableStateOf(true) }
    var afterFood by remember { mutableStateOf(false) }
    
    // State for expandable sections
    var showTimePicker by remember { mutableStateOf(false) }
    var showDurationPicker by remember { mutableStateOf(false) }
    
    // Time state for picker
    var hour by remember { mutableIntStateOf(8) }
    var minute by remember { mutableIntStateOf(0) }
    var is24Hour by remember { mutableStateOf(false) }
    
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Medication Reminder",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                


            }
    

            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Medication Name Field
            Text(
                text = "Name of the Medicine",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextField(
                value = medicationName,
                onValueChange = { medicationName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor =  Color.LightGray.copy(alpha = 0.4f),
                    unfocusedContainerColor =  Color.LightGray.copy(alpha = 0.4f),
                    disabledContainerColor =  Color.LightGray.copy(alpha = 0.4f),
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Medicine Type
            Text(
                text = "Medicine Type",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Medicine Type Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MedicineTypeOption(
                    icon = R.drawable.ic_medicine,
                    isSelected = selectedMedicineType == 0,
                    primaryColor = primaryColor,
                    onClick = { selectedMedicineType = 0 }
                )
                
                MedicineTypeOption(
                    icon = R.drawable.ic_capsule,
                    isSelected = selectedMedicineType == 1,
                    primaryColor = primaryColor,
                    onClick = { selectedMedicineType = 1 }
                )
                
                MedicineTypeOption(
                    icon = R.drawable.syringe ,
                    isSelected = selectedMedicineType == 2,
                    primaryColor = primaryColor,
                    onClick = { selectedMedicineType = 2 }
                )
                
                MedicineTypeOption(
                    icon = R.drawable.ic_drops,
                    isSelected = selectedMedicineType == 3,
                    primaryColor = primaryColor,
                    onClick = { selectedMedicineType = 3 }
                )
            }
    

            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quantity
            Text(
                text = "Quantity",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Quantity Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease button
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
    

                
                // Quantity display
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("%02d", quantity),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
    

                
                // Increase button
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
    

            }
    

            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Time Schedule
            Text(
                text = "Time Schedule",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Time and Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Time
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background( Color.LightGray.copy(alpha = 0.4f))
                        .clickable {
                                showTimePicker = !showTimePicker
                                if (showTimePicker) showDurationPicker = false
                           },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeSchedule,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
    

                
                // Duration
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background( Color.LightGray.copy(alpha = 0.4f))
                        .clickable { 
                            showDurationPicker = !showDurationPicker 
                            if (showDurationPicker) showTimePicker = false
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = duration,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Inline Time Picker
            AnimatedVisibility(
                visible = showTimePicker,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Time Picker
                        val timePickerState = rememberTimePickerState(
                            initialHour = hour,
                            initialMinute = minute
                        )
                        
                        TimePicker(
                            state = timePickerState,
                            modifier = Modifier.padding(8.dp),
                            colors = TimePickerDefaults.colors(
                                timeSelectorSelectedContainerColor = primaryColor,
                                timeSelectorSelectedContentColor = Color.White
                            )
                        )
                        
                        // Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showTimePicker = false }
                            ) {
                                Text("Cancel")
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Button(
                                onClick = { 
                                    hour = timePickerState.hour
                                    minute = timePickerState.minute
                                    val period = if (timePickerState.hour < 12) "AM" else "PM"
                                    val displayHour = if (is24Hour) timePickerState.hour else if (timePickerState.hour == 0) 12 else if (timePickerState.hour > 12) timePickerState.hour - 12 else timePickerState.hour
                                    timeSchedule = String.format("%02d.%02d %s", displayHour, timePickerState.minute, period)
                                    showTimePicker = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor
                                )
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
            
            // Inline Duration Picker
            AnimatedVisibility(
                visible = showDurationPicker,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select Duration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Duration List
                        val durations = listOf(
                            "1 Day", "2 Days", "3 Days", "5 Days", "7 Days", 
                            "10 Days", "14 Days", "21 Days", "30 Days", "60 Days", "90 Days"
                        )
                        
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            items(durations) { durationItem ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (durationItem == duration) primaryColor.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable { duration = durationItem }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = durationItem,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (durationItem == duration) FontWeight.Bold else FontWeight.Normal,
                                        color = if (durationItem == duration) primaryColor else Color.Black
                                    )
                                }
                            }
                        }
                        
                        // Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showDurationPicker = false }
                            ) {
                                Text("Cancel")
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Button(
                                onClick = { showDurationPicker = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor
                                )
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
    

            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Repeat
            Text(
                text = "Repeat",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Before/After Food Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Before Food Button
                Button(
                    onClick = { 
                        beforeFood = true
                        afterFood = false
                    }
    
,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (beforeFood) primaryColor.copy(alpha = 0.2f) else Color.White,
                        contentColor = if (beforeFood) primaryColor else Color.Gray
                    ),
                    border = if (!beforeFood) BorderStroke(2.dp, Color.LightGray) else BorderStroke(2.dp, primaryColor)
                ) {
                    Text(
                        text = "Before Food",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,

                    )
                }
    

                
                // After Food Button
                Button(
                    onClick = { 
                        beforeFood = false
                        afterFood = true
                    }
    
,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (afterFood) primaryColor.copy(alpha = 0.2f) else Color.White,
                        contentColor = if (afterFood) primaryColor else Color.Gray
                    ),
                    border = if (!afterFood) BorderStroke(2.dp, Color.LightGray) else BorderStroke(2.dp, primaryColor)
                ) {
                    Text(
                        text = "After Food",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
    

            }
    

            
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {},
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Add Reminder"

                )
            }
    

            Spacer(modifier = Modifier.height(32.dp))

        }


    }
    

}

@Composable
fun MedicineTypeOption(
    icon: Int,
    isSelected: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    // Get the label based on the icon resource ID
    val label = when (icon) {
        R.drawable.ic_medicine -> "Pill"
        R.drawable.ic_capsule -> "Capsule"
        R.drawable.syringe -> "Syringe"
        R.drawable.ic_drops -> "Drops"
        else -> ""
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(70.dp).height(70.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) primaryColor.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.4f))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) primaryColor else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = if (isSelected) primaryColor else Color.Gray,
            modifier = Modifier.size(28.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) primaryColor else Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = 10.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicationBottomSheetPreview() {
    AddMedicationBottomSheet(onDismiss = {})
}
