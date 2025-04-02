package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.ActivityCard
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.NotificationCard
import com.example.myapplication.viewmodel.MainViewModel

@Composable
fun ActivitiesScreen(

    navController: NavController


) {
    var currentRoute by remember { mutableStateOf("activities") }


    Scaffold (
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
            Row(modifier =Modifier.padding(12.dp).background(color = Color.LightGray.copy(alpha = 0.1f))  ,
                verticalAlignment =  Alignment.CenterVertically){

                IconButton(onClick = { navController.navigate("Dashboard") }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier.requiredSize(30.dp)
                    )
                }

                Text(
                    text="My Activities",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .padding(  bottom = 14.dp)
                    .fillMaxWidth().align(Alignment.CenterHorizontally)
            )

            Column (horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)){

                Row (

                ) {
                    Text(
                        text = "Edit",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.clickable { /* TODO: Handle edit */ }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){

            ActivityCard(
                title = "Medication",
                desc = "Set Your Reminders.",
                icon= painterResource(id = R.drawable.pill),
                navigate= "medication",
                navController = navController
            )
            Spacer(modifier = Modifier.height(10.dp))
            ActivityCard(
                title = "Mobility", desc = "Your Activity trends.",
                icon= painterResource(id = R.drawable.walk_ic),
                navigate= "stepcounter",
                navController = navController
            )

            Spacer(modifier = Modifier.height(10.dp))
            ActivityCard(
                title = "Water",  desc = "Track your water intake.",
                icon= painterResource(id = R.drawable.bottle_of_water_rafiki),
                navigate= "water" ,navController = navController
            )


            Spacer(modifier = Modifier.height(10.dp))
            ActivityCard(
                title = "Nutrition", desc = "Log your meals.",
                icon= painterResource(id = R.drawable.healthy_food_rafiki),
                navigate= "medication",
                navController = navController
            )
            Spacer(modifier = Modifier.height(10.dp))
            ActivityCard(
                title = "Sleep",  desc = "Track your sleep patterns.",
                icon= painterResource(id = R.drawable.ic_sleep),
                navigate= "sleep",
                navController = navController
            )
            Spacer(modifier = Modifier.height(10.dp))
            ActivityCard(
                title = "Vitals", desc = "fvmgdb",
                icon= painterResource(id = R.drawable.hand) ,navigate= "vitals",
                navController = navController

            )
            Spacer(modifier = Modifier.height(242.dp))
        }

        }


    }



}

@Preview
@Composable
fun ActivitiesScreenPreview() {
    val navController = rememberNavController()
    ActivitiesScreen(navController = navController)
}
