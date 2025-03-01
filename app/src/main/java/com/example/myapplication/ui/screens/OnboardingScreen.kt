package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    Column {



        Image(
            modifier = Modifier.width(250.dp),
            painter = painterResource(id =R.drawable.house_bookshelves_rafiki),
            contentDescription = "Quiet street",
            alignment = Alignment.Center
        )

        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier.width(50.dp).height(10.dp),


        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(
        onComplete = {}
    )
}
