package com.example.myapplication.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsCard(
    steps: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),  border = BorderStroke(0.3.dp, Color.LightGray)
    ) {
        Row  (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row (
                modifier = Modifier.weight(0.3f)
                , horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) { Icon(
                painter = painterResource(id = R.drawable.ic_steps),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
                Text(
                    text = "Steps",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )



            }
             Column(
                 modifier= Modifier.padding(top = 42.dp).weight(0.7f)
             ){
                 StepsProgressArc(
                     progress = steps.toFloat() / goal.toFloat(),
                     modifier = Modifier
                         .height(40.dp).padding(horizontal = 29.dp) ,
                 )
                 Row(
                     modifier = Modifier.fillMaxWidth()  ,
                     verticalAlignment = Alignment.Bottom,
                     horizontalArrangement = Arrangement.Center
                 ) {
                     Text(
                         text = steps.toString(),
                         style = MaterialTheme.typography.displaySmall,
                         fontWeight = FontWeight.Black
                     )
                     Text(
                         text = "/$goal",
                         style = MaterialTheme.typography.bodyLarge,
                         color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                         modifier = Modifier.padding( bottom = 6.dp, start = 4.dp)
                     )
                 }

             }





        }
    }
}

@Preview
@Composable
fun StepsCardPreview() {
    StepsCard(
        steps = 3500,
        goal = 10000
    )
} 