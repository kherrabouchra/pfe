package com.example.myapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.BetterAppTheme
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.viewmodel.FallDetectionViewModel
import com.example.myapplication.viewmodel.MainViewModel

class OnboardingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val fallDetectionViewModel: FallDetectionViewModel = viewModel()

            BetterAppTheme {
                AppNavigation(mainViewModel, fallDetectionViewModel)
            }
        }
    }
}
