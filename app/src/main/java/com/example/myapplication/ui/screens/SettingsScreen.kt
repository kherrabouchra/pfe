package com.example.myapplication.ui.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.model.UserProfile
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.EmergencyContact
import com.example.myapplication.ui.components.EmergencyContactCard
import com.example.myapplication.ui.components.EmergencyContactDialog
import com.example.myapplication.ui.components.UserProfileEditForm
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SettingsScreen(
    navController: NavController
) {    
    var currentRoute by remember { mutableStateOf("settings") }
    var wheelchairState by remember { mutableStateOf(false) }
    var medsState by remember { mutableStateOf(false) }
    var waterState by remember { mutableStateOf(false) }
    var showEmergencyContacts by remember { mutableStateOf(false) }
    var showEditProfile by remember { mutableStateOf(false) }
    var showEditProfileForm by remember { mutableStateOf(false) }
    
    // User profile state
    var userName by remember { mutableStateOf("Johnny") }
    var dateOfBirth by remember { mutableStateOf("23/03/1999(26)") }
    var medicalConditions by remember { mutableStateOf("Diabetes") }
    var allergies by remember { mutableStateOf("Peanuts, \nFish") }
    var bloodType by remember { mutableStateOf("B+") }
    
    // Emergency contact dialog state
    var showAddContactDialog by remember { mutableStateOf(false) }
    
    // List of emergency contacts
    val emergencyContacts = remember { mutableStateListOf<EmergencyContact>() }
    
    // Contact being edited (null if adding new contact)
    var editingContact by remember { mutableStateOf<EmergencyContact?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color.LightGray),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                navController = navController,
                onRouteChange = { currentRoute = it }
            )
        }
    ){ padding ->
        Column {
            // Top Bar with back button and title
            Row(modifier = Modifier.padding(12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { 
                        if (showEmergencyContacts) {
                            showEmergencyContacts = false
                        } else {
                            navController.navigate("Dashboard")
                        }
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Go Back",
                            modifier = Modifier.requiredSize(30.dp)
                        )
                    }

                    Text(
                        text = if (showEmergencyContacts) "Emergency Contacts" else "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Content based on whether emergency contacts are shown or not
            if (showEmergencyContacts) {
                // Emergency Contacts View
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)) {
                    
                    Text(
                        text = "Your Emergency Contact will receive a message with your current location in case you call or a fall is detected.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(0.3.dp, Color.LightGray),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                showAddContactDialog = true 
                            }
                            .padding(16.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AddCircleOutline,
                                contentDescription = "Add contact",
                                modifier = Modifier.requiredSize(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Add Emergency Contact",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (emergencyContacts.isEmpty()) {
                        Text(
                            text = "No emergency contacts added yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Column {
                            emergencyContacts.forEach { contact ->
                                EmergencyContactCard(
                                    contact = contact,
                                    onEdit = { 
                                        editingContact = it
                                        showAddContactDialog = true
                                    },
                                    onDelete = { contactToDelete ->
                                        emergencyContacts.removeIf { it.id == contactToDelete.id }
                                    }
                                )
                            }
                        }
                    }
                }
            } else if (showEditProfileForm) {
                // Edit Profile Form View
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        border = BorderStroke(0.3.dp, Color.LightGray),
                        modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                    ){
                        UserProfileEditForm(
                            name = userName,
                            dateOfBirth = dateOfBirth,
                            medicalConditions = medicalConditions,
                            allergies = allergies,
                            bloodType = bloodType,
                            isWheelchairUser = wheelchairState,
                            onNameChange = { userName = it },
                            onDateOfBirthChange = { dateOfBirth = it },
                            onMedicalConditionsChange = { medicalConditions = it },
                            onAllergiesChange = { allergies = it },
                            onBloodTypeChange = { bloodType = it },
                            onWheelchairChange = { wheelchairState = it },
                            onSave = {
                // Create a UserProfile object with the form data
                val userProfile = UserProfile(
                    userId = "user123", // In a real app, get this from Firebase Auth
                    name = userName,
                    // Parse age from dateOfBirth string if possible
                    age = try {
                        val ageStr = dateOfBirth.substringAfter("(").substringBefore(")")
                        ageStr.toIntOrNull() ?: 0
                    } catch (e: Exception) { 0 },
                    // Add other fields from form
                    healthConditions = if (medicalConditions.isNotBlank()) {
                        medicalConditions.split(",").map { it.trim() }
                    } else { listOf() },
                    mobilityLevel = if (wheelchairState) "Assisted" else "Normal"
                )
                
                // Create a coroutine scope for the Firebase operation
                kotlinx.coroutines.MainScope().launch {
                    try {
                        // Save to Firebase using UserRepository
                        val userRepository = UserRepository(FirebaseFirestore.getInstance())
                        val success = userRepository.updateUserProfile(userProfile)
                        
                        if (success) {
                            // Show success message (in a real app)
                            // For now, just close the form
                            showEditProfileForm = false
                        } else {
                            // Handle failure (in a real app, show error message)
                            // For now, just close the form
                            showEditProfileForm = false
                        }
                    } catch (e: Exception) {
                        // Handle error (in a real app, show error message)
                        e.printStackTrace()
                        showEditProfileForm = false
                    }
                }
            },
                            onCancel = { showEditProfileForm = false }
                        )
                    }
                }
            } else {
                // Main Settings View
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),
                        border = BorderStroke(0.3.dp, Color.LightGray),
                        modifier = Modifier.padding(horizontal = 18.dp).height(460.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                    ){
                        Column (modifier = Modifier.padding(22.dp).fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom=18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top){

                                Row (verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround){
                                    Image(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Profile picture",
                                        modifier = Modifier.requiredSize(78.dp).alpha(0.3f).shadow(
                                            elevation = 4.dp, shape= CircleShape
                                        )
                                    )
                                    Text(
                                        text="Johnny",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }

                                Text(
                                    text="Edit",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(10.dp).clickable { showEditProfileForm = true },
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween){
                                Text(
                                    text="Date of Birth",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text="23/03/1999(26)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth().align(Alignment.CenterHorizontally)
                            )
                            
                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween){
                                Text(
                                    text="Medical Conditions",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text="Diabetes",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                            }
                            
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth().align(Alignment.CenterHorizontally)
                            )
                            
                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween){
                                Text(
                                    text="Allergies",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text="Peanuts, \nFish",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                            }
                            
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth().align(Alignment.CenterHorizontally)
                            )
                            
                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween){
                                Text(
                                    text="Blood Type",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text="B+",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                            }
                            
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth().align(Alignment.CenterHorizontally)
                            )
                            
                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween){
                                Text(
                                    text="Wheelchair",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Switch(
                                    checked = wheelchairState,
                                    onCheckedChange = { wheelchairState = it }
                                )
                            }
                            
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth().align(Alignment.CenterHorizontally)
                            )
                            
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .height(49.dp)
                                .clickable { showEmergencyContacts = true },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Emergency contacts",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Icon(
                                    Icons.Default.ArrowForwardIos,
                                    contentDescription = "Go Forward",
                                    modifier = Modifier.requiredSize(18.dp)
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Push Notification",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    }

                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        ),  border = BorderStroke(0.3.dp, Color.LightGray),
                        modifier = Modifier.padding(horizontal = 18.dp).height(280.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                    ){
                        Column (modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp).fillMaxWidth()){
                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically){
                                Text(
                                    text="Medication Reminders",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                )
                                Switch(
                                    checked = medsState,
                                    onCheckedChange = { medsState = true  }
                                )
                            }
                            
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth().align(Alignment.CenterHorizontally)
                            )

                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically){
                                Text(
                                    text="Water Reminders",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                )
                                Switch(
                                    checked = waterState,
                                    onCheckedChange = { waterState = true  }
                                )
                            }
                            
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth().align(Alignment.CenterHorizontally)
                            )
                            
                            Row (modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically){
                                Text(
                                    text="Steps Reminders",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                )
                                Switch(
                                    checked = true,
                                    onCheckedChange = {   }
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth().align(Alignment.CenterHorizontally)
                            )

                            Row (modifier = Modifier.fillMaxWidth().height(49.dp).padding(vertical = 8.dp).clickable{},
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically){
                                Text(
                                    text="Voice commands",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Icon(
                                    Icons.Default.ArrowForwardIos,
                                    contentDescription = "Go Forward",
                                    modifier = Modifier.requiredSize(18.dp)
                                )}
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "Export Health Data",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = "LogOut",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Red,
                        )
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
        
        // Emergency Contact Dialog - Moved outside conditional blocks to ensure it's always rendered when needed
        if (showAddContactDialog) {
            EmergencyContactDialog(
                onDismissRequest = { 
                    showAddContactDialog = false
                    editingContact = null
                },
                onConfirm = { name, phoneNumber, relationship ->
                    if (editingContact != null) {
                        // Edit existing contact
                        val index = emergencyContacts.indexOfFirst { it.id == editingContact!!.id }
                        if (index != -1) {
                            emergencyContacts[index] = EmergencyContact(
                                id = editingContact!!.id,
                                name = name,
                                phoneNumber = phoneNumber,
                                relationship = relationship
                            )
                        }
                    } else {
                        // Add new contact
                        emergencyContacts.add(
                            EmergencyContact(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                phoneNumber = phoneNumber,
                                relationship = relationship
                            )
                        )
                    }
                    showAddContactDialog = false
                    editingContact = null
                },
                // Pass the existing contact for editing
                existingContact = editingContact
            )
        }
        
        // Profile Edit Form Dialog is now integrated into the main conditional flow
        // and no longer rendered here to prevent overlapping
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    // Create a mock NavController for preview purposes
    val navController = rememberNavController() // Use a mock NavController
    SettingsScreen(navController = navController) // Pass the navController
}