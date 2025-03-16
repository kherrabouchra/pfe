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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@Composable

fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

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
                .height(28.dp)
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
        Spacer(modifier = Modifier.height(36.dp))
        // Navigation buttons with icons
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
                        modifier = Modifier.requiredSize(34.dp)
                    )
                }
            } else {
                IconButton(onClick = onComplete) {
                    Icon(
                        Icons.Default.ArrowForwardIos,
                        contentDescription = "Get Started",
                        modifier = Modifier.requiredSize(38.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun OnboardingPage(page: Int) {
    val pageData = listOf(
        "Welcome to Better! \nYour all-in-one place wellness assistant.",
        "Stay organized and keep track of all your health data in one place.",
        "Wether you need long or short term assistance, Perform your daily life activities according to your needs.",
        "You're all set! Let's get started."
    )

    val images = listOf(
        R.drawable.better_logo,
        R.drawable.personal_data_rafiki,
        R.drawable.medical_care_rafiki,
        R.drawable.hospital_patient_rafiki
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .background(Color.White), // Set background color
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Column (verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()){
           Row (
           ) {
               Image(
                   modifier = Modifier.width(
                       if(page == 0) 150.dp else 300.dp),
                   painter = painterResource(id = images[page]),
                   contentDescription = "Illustration"
               )

           }

            if (page == 2) (
                    Row() {
                        Image(
                            modifier = Modifier.width(
                                if(page == 0) 150.dp else 300.dp),
                            painter = painterResource(id = R.drawable.hospital_patient_rafiki),
                            contentDescription = "Illustration"
                        )
                    }

                    )

        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pageData[page],
            fontSize = 18.sp,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 22.dp),

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
