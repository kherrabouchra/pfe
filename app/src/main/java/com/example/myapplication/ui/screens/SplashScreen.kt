package com.example.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreenContent(onSplashComplete: () -> Unit) {
    var isAnimationPlayed by remember { mutableStateOf(false) }

    val alphaAnim = animateFloatAsState(
        targetValue = if (isAnimationPlayed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "Alpha Animation"
    )

    LaunchedEffect(key1 = true) {
        isAnimationPlayed = true
        delay(2000) // Wait for fade-in plus a small delay
        onSplashComplete() // Call the navigation or next screen action here
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row (verticalAlignment = Alignment.Bottom){

            Image(
                painter = painterResource(id = R.drawable.logochar),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(100.dp).alpha(alphaAnim.value)
            )
          
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreenContent(
        onSplashComplete = {}
    )
}
