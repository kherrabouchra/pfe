package com.example.myapplication.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.components.HorizontalCalendar
import com.example.myapplication.ui.components.MedicationCard
import com.example.myapplication.ui.components.AddMedicationBottomSheet
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.viewmodel.MedicationViewModel
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationScreen(
    navController: NavController,
    medicationViewModel: MedicationViewModel = viewModel()
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf("activities") }
    
    // Collect medications from ViewModel
    val medications by medicationViewModel.medications.collectAsState()
    val isLoading by medicationViewModel.isLoading.collectAsState()
    
    // Moved filteredMedications to the correct scope level
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val filteredMedications = medications.filter { medication ->
        medication.timeOfDay.any { dateTime ->
            dateTime.toLocalDate() == selectedDate
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(46.dp)).background(Color.Black)
                ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Medication",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                navController = navController,
                onRouteChange = { currentRoute = it }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(color = Color.LightGray.copy(alpha = 0.1f))) {
            Row(modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate("activities") }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Medication",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // Calendar with date selection to fetch medications for selected date
                    // Removed duplicate selectedDate declaration
                    // Removed duplicate filteredMedications declaration
                    
                    HorizontalCalendar(
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            selectedDate = date
                            // Date selection is handled by the filtered medications above
                        }
                    )

                    Row(modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically){
                      Column (modifier = Modifier.weight(2F)){
                          Text(
                              text = "Track Your Medications",
                              style = MaterialTheme.typography.headlineSmall,
                              textAlign = TextAlign.Start,
                              fontWeight = FontWeight.Black,
                          )
                          Text(
                              text = "Set reminders and take your medications on time.",
                              style = MaterialTheme.typography.titleSmall,
                              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                              textAlign = TextAlign.Start,
                               
                          )
                          Button(
                              onClick = { showBottomSheet = true },
                              modifier = Modifier.height(42.dp).padding(top = 8.dp),
                              colors = ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.primary,
                                  contentColor = Color.White
                              )
                          ) {
                              Text(text = "Add Reminder"

                              )
                          }
                      }
                        Column(modifier = Modifier.weight(1.5f)) {
                            Image(
                                painter = painterResource(id = R.drawable.remedy_rafiki),
                                contentDescription = "medication",
                                modifier = Modifier.requiredSize(190.dp)
                            )
                        }
                    }
                   /* DismissibleCard(
                        navController = navController,
                        title = "Track Your Medications",
                        description = "Set reminders and track your medication intake all in one place.",
                        imageRes = R.drawable.remedy_rafiki
                    )*/
                }
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
            text = "Your Medications",
            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Text(
                text = "View All",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.clickable { /* TODO: Handle navigation */ }
                        )
                    }
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else if (filteredMedications.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(56.dp))
                            Image(
                                painter = painterResource(id = R.drawable.pillsearch),
                                contentDescription = "medication",
                                modifier = Modifier.size(160.dp)
                            )
                            Text(
                                text = "You have no medications\n reminders yet.",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        filteredMedications.forEach { medication ->
                            MedicationCard(
                                title = medication.name,
                                subtitle = "${medication.dosage} - ${medication.frequency}",
                                time = medication.timeOfDay.firstOrNull()?.toLocalTime()?.let { 
                                    String.format("%02d:%02d", it.hour, it.minute) 
                                } ?: "",
                                id = medication.id,
                                navController = navController
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(342.dp))
            }
        }


    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }, sheetState = bottomSheetState, containerColor = Color.White

        ) {
            AddMedicationBottomSheet(onDismiss = { showBottomSheet = false })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun MedicationScreenPreview() {
    val navController = rememberNavController()
    MedicationScreen(navController = navController)
}
