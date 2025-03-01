package com.example.myapplication.ui.screens

 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.layout.height
 import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.layout.requiredSize
 import androidx.compose.foundation.layout.width
 import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
 import androidx.compose.material3.HorizontalDivider
 import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
 import com.example.myapplication.ui.components.NotificationCard
 import com.example.myapplication.viewmodel.MainViewModel

@Composable
fun NotificationsScreen(

    navController: NavController


) {


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){


Column {
    Row(modifier =Modifier.padding(12.dp)  ,
        verticalAlignment =  Alignment.CenterVertically){

        IconButton(onClick = { navController.navigate("Dashboard") }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Go Back",
                modifier = Modifier.requiredSize(30.dp)
            )
        }

        Text(
            text="Notifications",
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



    NotificationCard(
        title = "reminder ",
        subtitle = "rkdfsnlnslevnse",
        time= "20:12"
    )
}


    }



}

@Preview
@Composable
fun NotificationsScreenPreview() {
     val navController = rememberNavController()
    NotificationsScreen(navController = navController)
}
