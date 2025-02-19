package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.AIChatScreen
import com.example.myapplication.ui.screens.*
import com.example.myapplication.viewmodel.AIViewModel
import com.example.myapplication.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")

    object AIChat : Screen("AIChat")
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreenContent(
                onSplashComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true } // To prevent back navigation
                    }
                }
            )
        }

        composable(Screen.AIChat.route) {
            val aiViewModel: AIViewModel = viewModel()
            AIChatScreen(aiViewModel)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }
    }
}
