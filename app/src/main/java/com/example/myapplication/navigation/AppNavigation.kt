package com.example.myapplication.navigation

import android.app.Notification
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.myapplication.viewmodel.FallDetectionViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Signup : Screen("signup")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Fall : Screen("FallDetectionScreen")
    object AIChat : Screen("AIChat")
    object Settings : Screen("settings")
    object Notifications : Screen("notifications")
    object Medication : Screen("medication")
    object Activities : Screen("activities")
    object Reminder : Screen("reminder")
}

@RequiresApi(Build.VERSION_CODES.O)
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

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = { 
                    navController.navigate(Screen.Signup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onSignup = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AIChat.route) {
            val aiViewModel: AIViewModel = viewModel()
            AIChatScreen(aiViewModel, navController)
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

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.Notifications.route) {
            NotificationsScreen(navController)
        }
        composable(Screen.Activities.route) {
            ActivitiesScreen(navController)
        }
        composable(Screen.Medication.route) {
           MedicationScreen(navController)
        }
        composable(Screen.Reminder.route) {
            ReminderScreen(navController)
        }
        composable(Screen.Fall.route) {
            val FallDetectionViewModel: FallDetectionViewModel = viewModel()
            FallDetectionScreen( FallDetectionViewModel, {},navController,)
        }
    }
}
