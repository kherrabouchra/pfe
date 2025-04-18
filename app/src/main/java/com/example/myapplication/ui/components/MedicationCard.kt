package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.Screen

@Composable
fun MedicationCard(
    title: String,
    subtitle: String,
    time: String,
    id: String,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).height(90.dp).clickable( onClick = {
            navController.navigate(Screen.Reminder.route) {
                popUpTo(Screen.Dashboard.route) { inclusive = true }
            }
        }),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically

            ) {

            Column {
                Image(
                    painter = painterResource(id = R.drawable.pill_ic),
                    contentDescription = "medication",
                    modifier = Modifier.size(46.dp),

                    )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp).fillMaxWidth(),

                ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,


                ){

                    Text(
                        text = title + " " + "at"+" ",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = time,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun MedicationCardPreview() {
    val navController = rememberNavController()
    MedicationCard(
        title = "Daily Exercise",
        subtitle = "30 minutes of moderate activity",
        time = "18:13",
        id = "1",
        navController = navController
    )
}