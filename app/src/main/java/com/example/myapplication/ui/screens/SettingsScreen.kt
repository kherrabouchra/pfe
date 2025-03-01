package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.BottomNavigationBar

@Composable
fun SettingsScreen(
    navController: NavController
) {    var currentRoute by remember { mutableStateOf("settings") }
    var wheelchairState by remember { mutableStateOf(false) }
    var medsState by remember { mutableStateOf(false) }
    var waterState by remember { mutableStateOf(false) }

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
            Row(modifier =Modifier.padding(12.dp).fillMaxWidth()  ,
                verticalAlignment =  Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){

                Row(  verticalAlignment =  Alignment.CenterVertically) {
                    IconButton(onClick = { navController.navigate("Dashboard") }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Go Back",
                            modifier = Modifier.requiredSize(30.dp)
                        )
                    }

                    Text(
                        text="Account Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )

                }

                IconButton(onClick = { navController.navigate("Dashboard") }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Go Back",
                        modifier = Modifier.requiredSize(30.dp)
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .padding(  bottom = 14.dp)
                    .fillMaxWidth().align(Alignment.CenterHorizontally)
            )
            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                   
                }
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp,

                    ),
                modifier = Modifier.padding(horizontal = 14.dp).height(300.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
            ){
                Column (modifier = Modifier.padding(22.dp).fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom=18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top){

                    Image(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile picture",
                        modifier = Modifier.requiredSize(78.dp),

                    )
                    Text(
                        text="Edit",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.primary


                    )

                }
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween){
                    Text(
                        text="Full Name",
                        style = MaterialTheme.typography.titleSmall,
                        )
                    Text(
                        text="Full Name",
                        style = MaterialTheme.typography.titleMedium,

                        )

                }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(  vertical = 10.dp)
                            .fillMaxWidth().align(Alignment.CenterHorizontally)
                    )

                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween){
                        Text(
                            text="Date of Birth",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text="Full Name",
                            style = MaterialTheme.typography.titleMedium,

                            )

                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(  vertical = 10.dp)
                            .fillMaxWidth().align(Alignment.CenterHorizontally)
                    )
                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween){
                        Text(
                            text="Blood Type",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text="B+",
                            style = MaterialTheme.typography.titleMedium,

                            )

                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(  vertical = 10.dp)
                            .fillMaxWidth().align(Alignment.CenterHorizontally)
                    )
                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(
                            text="Wheelchair",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Switch(
                            checked = wheelchairState,
                            onCheckedChange = { wheelchairState = true }
                        )

                    }

                }

            }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Push Notification",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )


                }

                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp,

                        ),
                    modifier = Modifier.padding(horizontal = 14.dp).height(210.dp).fillMaxWidth(),
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
                        )
                            Switch(
                                checked = medsState,
                                onCheckedChange = { medsState = true  }
                            )

                    }
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(  vertical = 10.dp)
                                .fillMaxWidth().align(Alignment.CenterHorizontally)
                        )

                        Row (modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text="Water Reminders",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Switch(
                                checked = waterState,
                                onCheckedChange = { waterState = true  }
                            )

                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(  vertical = 10.dp)
                                .fillMaxWidth().align(Alignment.CenterHorizontally)
                        )
                        Row (modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically){
                            Text(
                                text="Medication Reminders",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Switch(
                                checked = true,
                                onCheckedChange = {   }
                            )

                        }

                    }


                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(  vertical = 10.dp)
                        .fillMaxWidth().align(Alignment.CenterHorizontally)
                )
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
                    Spacer(modifier = Modifier.height(42.dp))

                }
            }
        }

    }


}

@Preview
@Composable
fun SettingsScreenPreview() {
    // Create a mock NavController for preview purposes
    val navController = rememberNavController() // Use a mock NavController
    SettingsScreen(navController = navController) // Pass the navController
} 