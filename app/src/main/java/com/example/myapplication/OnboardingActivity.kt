package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.BetterAppTheme
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.viewmodel.MainViewModel

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = viewModel()
            BetterAppTheme {
                AppNavigation(viewModel)
            }
        }
    }
} 