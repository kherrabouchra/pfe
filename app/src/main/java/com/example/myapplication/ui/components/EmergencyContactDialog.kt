package com.example.myapplication.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.window.Dialog

@Composable
fun EmergencyContactDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, phoneNumber: String, relationship: String) -> Unit,
    existingContact: EmergencyContact? = null
) {
    // Initialize with existing contact data if provided
    var name by remember { mutableStateOf(existingContact?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(existingContact?.phoneNumber ?: "") }
    var relationship by remember { mutableStateOf(existingContact?.relationship ?: "") }
    
    // Validation states
    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (existingContact != null) "Edit Emergency Contact" else "Add Emergency Contact",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Name Field
                Text(
                    text = "Contact Name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = name,
                    onValueChange = { 
                        name = it 
                        nameError = it.isEmpty()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.4f)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                        unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.4f),
                        errorContainerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Name cannot be empty") }
                    } else null
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Phone Number Field
                Text(
                    text = "Phone Number",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = phoneNumber,
                    onValueChange = { 
                        phoneNumber = it 
                        phoneError = it.isEmpty() || !it.all { char -> char.isDigit() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.4f)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                        unfocusedContainerColor = Color.LightGray.copy(alpha = 0.4f),
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.4f),
                        errorContainerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError,
                    supportingText = if (phoneError) {
                        { Text("Please enter a valid phone number") }
                    } else null
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Relationship Field
                Text(
                    text = "Relationship",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = relationship,
                    onValueChange = { relationship = it },
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
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { 
                            nameError = name.isEmpty()
                            phoneError = phoneNumber.isEmpty() || !phoneNumber.all { it.isDigit() }
                            
                            if (!nameError && !phoneError) {
                                onConfirm(name, phoneNumber, relationship)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}