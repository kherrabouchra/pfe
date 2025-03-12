package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
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
import com.example.myapplication.R
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
                        text="Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )

                }

                IconButton(onClick = { navController.navigate("Dashboard") }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.requiredSize(30.dp)
                    )
                }
            }

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
                            text="Park Minji",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 16.dp)

                        )

                    }

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
                            .padding(  vertical = 10.dp)
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
                            .padding(  vertical = 10.dp)
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
                            .padding(  vertical = 10.dp)
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
                            .padding(  vertical = 10.dp)
                            .fillMaxWidth().align(Alignment.CenterHorizontally)
                    )
                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(
                            text="Wheelchair",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Switch(
                            checked = wheelchairState,
                            onCheckedChange = { wheelchairState = true }
                        )

                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(  vertical = 10.dp)
                            .fillMaxWidth().align(Alignment.CenterHorizontally)
                    )

                    Row (modifier = Modifier.fillMaxWidth().height(49.dp).padding(bottom = 8.dp).clickable{},
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(
                            text="Caregivers",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Icon(
                            Icons.Default.ArrowForwardIos,
                            contentDescription = "Go Forward",
                            modifier = Modifier.requiredSize(18.dp)
                        )
                    }

                }

            }

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Emergency Contact",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )


                }

                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),  border = BorderStroke(0.3.dp, Color.LightGray),
                    modifier = Modifier.padding(horizontal = 18.dp).height(50.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                ){
                    Column (modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp).fillMaxWidth()){



                        Row (modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically){
                            Icon(
                                Icons.Default.AddCircleOutline,
                                contentDescription = "add contact",
                                modifier = Modifier.requiredSize(32.dp),
                             )
                            Text(
                                text="Add Emergency Contact",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier =Modifier.padding(start = 16.dp)
                            )


                        }


                    }


                }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Your Emergency Contact will receive a message with your current location in case you call or a fall is detected.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )


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
                    modifier = Modifier.padding(horizontal = 18.dp).height(210.dp).fillMaxWidth(),
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
                                .padding(  vertical = 10.dp)
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
                                .padding(  vertical = 10.dp)
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
                    Spacer(modifier = Modifier.height(342.dp))

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