package com.example.myapplication.ui.screens
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.myapplication.R
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.BottomNavigationBar

@Composable
fun CaregiversScreen(
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
                        text="Caregivers",
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
            }}}}


@Preview
@Composable
fun CaregiversScreenPreview() {
    // Create a mock NavController for preview purposes
    val navController = rememberNavController() // Use a mock NavController
    CaregiversScreen(navController = navController) // Pass the navController
}