package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.viewmodel.FallDetectionViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FallDetectionScreen(
    viewModel: FallDetectionViewModel,
    onResetDetection: () -> Unit,
    navController: NavController

) {
    val fallAlertText by viewModel.fallAlertText.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            IconButton(modifier = Modifier.align(Alignment.Start),
                onClick = { navController.navigate("Dashboard") }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Go Back",
                    modifier = Modifier.requiredSize(24.dp).align(Alignment.Start)
                )
            }
           Spacer(modifier=Modifier.padding(vertical = 70.dp))
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {

                Image(
                    modifier = Modifier.width(250.dp),
                    painter = painterResource(id = R.drawable.epilepsy_rafiki),
                    contentDescription = "Quiet street",
                    alignment = Alignment.Center
                )
                Text(
                    text = if (fallAlertText.isEmpty()) "Enable Fall Detection" else fallAlertText,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black,

                )
                Text(
                    text = if (fallAlertText.isEmpty()) "Enable Fall Detection" else fallAlertText,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,

                    modifier = Modifier.padding(bottom = 16.dp)
                )

              Switch(
                  checked = true,
                  onCheckedChange = {   }
              )
            }


            if (fallAlertText.isNotEmpty()) {
                Button(onClick = onResetDetection) {
                    Text("OK")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun FallDetectionScreenPreview() {
    val navController = rememberNavController() // Use a mock NavController
    FallDetectionScreen(
        navController = navController,
        viewModel = FallDetectionViewModel(),
        onResetDetection = {  }
    ) // Pass the navController

} 