package com.example.myapplication.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.MedicationScreen
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationBottomSheet(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(24.dp)
            .height(740.dp)
    ) {
        Text(
            text = "Add Medication Reminder",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(18.dp))
        Column(modifier = Modifier){

            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Medication Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = "",
                onValueChange = {},
                label = { Text("Dosage") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

     Column(horizontalAlignment = Alignment.CenterHorizontally, modifier =Modifier.fillMaxWidth() ) {
    DialExample(onDismiss={}, onConfirm = {})
}

        Button(
            onClick = {
                // Handle the add medication action
                onDismiss() // Dismiss the bottom sheet after adding
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Medication")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialExample(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Column (horizontalAlignment = Alignment.CenterHorizontally){
        TimePicker(
            state = timePickerState,
        )

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun AddMedicationBottomSheetPreview() {
    AddMedicationBottomSheet( onDismiss = {})
}
