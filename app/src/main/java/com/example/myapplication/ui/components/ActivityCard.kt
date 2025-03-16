package com.example.myapplication.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
fun ActivityCard(
    title: String,
    desc: String,
    icon: Painter,
    navigate : String,
    navController : NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable (onClick = { navController.navigate(
            navigate){
            popUpTo(Screen.Dashboard.route) { inclusive = true }
        } } ), border = BorderStroke(0.4.dp, Color.LightGray),




        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

            ) {

            Row (verticalAlignment = Alignment.CenterVertically){

                Column {

                    Image(
                        painter = icon,
                        contentDescription = "Notifications"
                        ,modifier = Modifier.size(56.dp)
                    )

                }
                Column(
                    modifier = Modifier.padding(start = 16.dp),

                    ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween

                    ){

                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )


                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = desc,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

            Column  {
                Icon(

                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),

                    )
            }
        }
    }
}

@Preview
@Composable
fun ActivityCardPreview() {
    val navController = rememberNavController()
    ActivityCard(
        title = "Water intake",
        desc = "refvkn",
        icon = painterResource(id = R.drawable.pill),
        navigate = "medication",
        navController = navController
    )
}