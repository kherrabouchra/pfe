package com.example.myapplication.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.DismissibleCard
import com.example.myapplication.ui.components.HorizontalCalendar
import com.example.myapplication.ui.components.NotificationCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VitalsScreen(

    navController: NavController


) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf("activities") }


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

        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = { navController.navigate("activities") }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.requiredSize(30.dp)
                    )
                }

                Text(
                    text = "Vitals",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .fillMaxWidth().align(Alignment.CenterHorizontally)
            )



            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                HorizontalCalendar {}
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
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(horizontal = 6.dp)
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
                            modifier = Modifier.size(160.dp)
                        )
                    }
                }
               /* DismissibleCard(
                    navController = navController,
                    title = "Track Your Vitals",
                    description = "Set reminders and track your medication intake all in one place.",
                    imageRes = R.drawable.circulatory_system_rafiki
                )*/
            }
        }

    }
    }





@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun VitalsScreenPreview() {
    val navController = rememberNavController()
    VitalsScreen(navController = navController)
}
