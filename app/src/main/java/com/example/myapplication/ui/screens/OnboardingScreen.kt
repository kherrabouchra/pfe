package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

@Composable

fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()
    
    // State to control the animation
    var shouldExit by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    
    // Animation values
    val slideOffsetY by animateFloatAsState(
        targetValue = if (shouldExit) with(density) { 1000.dp.toPx() } else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        finishedListener = { if (shouldExit) onComplete() }
    )
    
    // Add a Box to contain the entire screen content and the close button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = slideOffsetY.dp / density.density)
    ) {

        IconButton(
            onClick = {  shouldExit = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .size(48.dp)
                .zIndex(1f) // <-- Make sure it's above everything
                .background(Color.White.copy(alpha = 0.8f), shape = CircleShape)
                .clip(CircleShape)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close Onboarding",
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        )  {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPage(page)
        }

        // Page indicator dots
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp)) // Clip before background
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) // Apply background after clipping
                .height(24.dp)
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))


        // Navigation buttons with icons
        if(pagerState.currentPage < 3){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = if (pagerState.currentPage == 0) Arrangement.End else Arrangement.SpaceBetween
        ) {
            val currentPage by remember { derivedStateOf { pagerState.currentPage } } // Ensure recomposition

            LaunchedEffect(pagerState.currentPage) {
            }

            // Back button
            if (currentPage > 0) {
                IconButton(onClick = {
                    scope.launch { pagerState.animateScrollToPage(currentPage - 1) }
                }) {
                    Icon(
                        Icons.Default.ArrowBackIos,
                        contentDescription = "Go Back",
                        modifier = Modifier.requiredSize(38.dp)
                    )
                }
            }

            // Forward/Next or Get Started button
            if (currentPage < pagerState.pageCount - 1) {
                IconButton(onClick = {
                    scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                }) {
                    Icon(
                        Icons.Default.ArrowForwardIos,
                        contentDescription = "Go Forward",
                        modifier = Modifier.requiredSize(30.dp)
                    )
                }
            } else {

                    IconButton(onClick = onComplete) {
                        Icon(
                            Icons.Default.ArrowForwardIos,
                            contentDescription = "Get Started",
                            modifier = Modifier.requiredSize(30.dp)
                        )
                    }



            }
        }}

        if(pagerState.currentPage == 3){
            Spacer(modifier = Modifier.height(18.dp))
            Button(onClick = onComplete,
                modifier = Modifier.fillMaxWidth(0.5f).height(48.dp))
            {Text("Get Started", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold
            )}
        }

        Spacer(modifier = Modifier.height(6.dp))
    }
    }
}

@Composable
fun OnboardingPage(page: Int) {
 

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .background(Color.White), // Set background color
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){






        if(page == 0){
            Column (verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()){

             }

            Image(
                modifier = Modifier.width(280.dp),
                painter = painterResource(id =R.drawable.personal_data_rafiki),
                contentDescription = "Illustration"
            )


            Text(
                text ="Empower Your Independence!",
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
                fontWeight = FontWeight.Black

                )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Track your daily activities independently and stay in control of your routine.",
                fontSize = 18.sp,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 22.dp),
                color = Color.Gray

                )
        }
        if(page == 1){
            Image(
                modifier = Modifier.width(280.dp),
                painter = painterResource(id =R.drawable.online_doctor_rafiki),
                contentDescription = "Illustration"
            )
            Text(
                text ="Get Personalized Assistance!",
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp),
                fontWeight = FontWeight.Black

            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Whether you need gentle nudges or detailed tracking, We adjust to your needs.",
                fontSize = 18.sp,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 22.dp),
                color = Color.Gray

            )
        }
        if(page == 2){
            Image(
                modifier = Modifier.width(270.dp),
                painter = painterResource(id =R.drawable.ambulance_amico),
                contentDescription = "Illustration"
            )
            Text(
                text ="Stay Safe & Connected!",
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp),
                fontWeight = FontWeight.Black

            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = " Detect early signs of risks, receive timely alerts, and stay connected with caregivers.",
                fontSize = 18.sp,
                fontWeight = FontWeight.W500,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 22.dp),
                color = Color.Gray

            )
        }

          if (page == 3){
              Row(verticalAlignment = Alignment.Bottom,
                  horizontalArrangement = Arrangement.Center, ){

                  Text(
                      text ="Welcome to",
                      fontSize = 24.sp,
                      fontWeight = FontWeight.Black,
                      textAlign = TextAlign.Center,
                      modifier = Modifier.padding(horizontal = 6.dp),

                      )
                  Image(
                      modifier = Modifier.width(110.dp),
                      painter = painterResource(id =  R.drawable.better_logo),
                      contentDescription = "Illustration"
                  )
              }
          }

    }
}

@Preview(showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(
        onComplete = {}
    )
}
