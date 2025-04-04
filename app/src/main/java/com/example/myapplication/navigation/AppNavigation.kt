package com.example.myapplication.navigation

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.AIChatScreen
import com.example.myapplication.ui.screens.*
import com.example.myapplication.viewmodel.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Signup : Screen("signup")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Fall : Screen("fall_detection")
    object AIChat : Screen("ai_chat")
    object Settings : Screen("settings")
    object Notifications : Screen("notifications")
    object Medication : Screen("medication")
    object Activities : Screen("activities")
    object Reminder : Screen("reminder")
    object Vitals : Screen("vitals")
    object Sleep : Screen("sleep")
    object StepCounter : Screen("stepcounter")
    object Questionnaire : Screen("questionnaire")
    object Water : Screen("water")
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    fallDetectionViewModel: FallDetectionViewModel? = null, // Make it optional
    startDestination: String = Screen.Splash.route // Add parameter for custom start destination
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.Splash.route) {
            SplashScreenContent(
                onSplashComplete = {
                    // Check if we should navigate to Fall screen (from notification) or normal flow
                    if (startDestination == Screen.Fall.route) {
                        // This means app was launched from a fall detection notification
                        navController.navigate(Screen.Fall.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        // Normal app launch flow - go to onboarding
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
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
                onSignup = { navController.navigate(Screen.Questionnaire.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }
        
        composable(Screen.Questionnaire.route) {
            QuestionnaireScreen(
                navController = navController,
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Questionnaire.route) { inclusive = true }
                    }
                }
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
                viewModel = mainViewModel,
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = false }
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
        composable(Screen.Vitals.route) {
            VitalsScreen(navController)
        }
        
        composable(Screen.Sleep.route) {
            SleepScreen(navController)
        }
        
        composable(Screen.Water.route) {
            WaterScreen(navController)
        }
        


        composable("heart_rate_monitor") {
            HeartRateMonitorScreen(
                navController = navController,
                onMeasurementComplete = { heartRate ->
                    navController.navigateUp()
                }
            )
        }

        // Only include FallDetectionScreen if fallDetectionViewModel is provided
        if (fallDetectionViewModel != null) {
            composable(Screen.Fall.route) {
                FallDetectionScreen(
                    viewModel = fallDetectionViewModel,
                    onResetDetection = { fallDetectionViewModel.resetFallAlert() },
                    navController = navController
                )
            }
        }
    }
}
