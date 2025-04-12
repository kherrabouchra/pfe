package com.example.myapplication.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
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
    navigate: String,
    navController: NavController,
    isEditMode: Boolean = false,
    isSelected: Boolean = false,
    onSelectToggle: () -> Unit = {}
) {
    val border = if (isEditMode && isSelected) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    } else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable {
                if (isEditMode) {
                    onSelectToggle()
                } else {
                    navController.navigate(navigate) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            },
        border = border,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = icon,
                    contentDescription = "Activity Icon",
                    modifier = Modifier.size(54.dp)
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = if (isEditMode) {
                    if (isSelected) Icons.Default.CheckCircle else Icons.Default.AddCircleOutline
                } else {
                    Icons.Default.ArrowForwardIos
                },
                contentDescription = null,
                modifier = Modifier
                    .size(if (isSelected)28.dp else 30.dp)
                    .padding(end = 6.dp),
                tint = if (isEditMode && isSelected) MaterialTheme.colorScheme.primary else Color.Gray
            )
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