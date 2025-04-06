package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.BetterButton
import com.example.myapplication.ui.components.SelectButton

@Composable
fun SmartWatchConnectionQuestion(
    hasSmartWatch: Boolean?,
    onHasSmartWatchChange: (Boolean) -> Unit,
    smartWatchType: String,
    onSmartWatchTypeChange: (String) -> Unit,
    isConnected: Boolean,
    onConnectClick: () -> Unit
) {
    Column {
        Text(
            text = "⌚ Smartwatch Connection",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Do you have a smartwatch you'd like to connect?",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            YesNoOption("Yes", hasSmartWatch == true) { onHasSmartWatchChange(true) }
            Spacer(modifier = Modifier.width(8.dp))
            YesNoOption("No", hasSmartWatch == false) { onHasSmartWatchChange(false) }
        }
        
        if (hasSmartWatch == true) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Select your smartwatch type:",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
                listOf("Apple Watch", "Samsung Galaxy Watch", "Fitbit", "Garmin", "Other").forEach { type ->
                    SmartWatchOption(
                        type = type,
                        isSelected = smartWatchType == type,
                        onSelect = { onSmartWatchTypeChange(type) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Connection status and button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isConnected) Color(0xFFE6F4EA) else Color(0xFFF5F7FF),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Watch,
                            contentDescription = "Smartwatch",
                            tint = if (isConnected) Color(0xFF34A853) else Color(0xFF2547CE),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isConnected) "Connected" else "Not Connected",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (isConnected) Color(0xFF34A853) else Color(0xFF2547CE)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (!isConnected) {
                        Button(
                            onClick = onConnectClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2547CE))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Connect",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connect Now")
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Connected",
                                tint = Color(0xFF34A853),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Your smartwatch is connected and ready to use",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF34A853)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Connecting your smartwatch enables fall detection, activity tracking, and health monitoring features.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SmartWatchOption(
    type: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect),
        color = if (isSelected) Color(0xFFE3E8FF) else MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2547CE))
            )
            Text(
                text = type,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}