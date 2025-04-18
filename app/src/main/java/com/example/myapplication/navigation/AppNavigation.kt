package com.example.myapplication.navigation

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.chat.AIChatScreen
import com.example.myapplication.data.HeartRateResult
import com.example.myapplication.ui.screens.*
import com.example.myapplication.viewmodel.*
import java.time.LocalDateTime

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
    object Symptoms : Screen("symptoms")
    object Nutrition : Screen("nutrition")
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
        startDestination = startDestination
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

        composable(
            route = Screen.Medication.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            val medicationViewModel: MedicationViewModel = viewModel(
                factory = MedicationViewModel.provideFactory()
            )
            MedicationScreen(navController = navController, medicationViewModel = medicationViewModel)
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
        
        composable(Screen.Symptoms.route) {
            SymptomsScreen(navController)
        }
        
        composable(Screen.Nutrition.route) {
            NutritionScreen(navController)
        }

        composable("heart_rate_monitor") {
            val vitalsViewModel: VitalsViewModel = viewModel()
            HeartRateMonitorScreen(
                navController = navController,
                onMeasurementComplete = { heartRate: Int ->
                    vitalsViewModel.setLastHeartRate(heartRate)
                    navController.navigate("heart_rate_result/$heartRate")
                }
            )
        }
        
        composable(
            route = "heart_rate_result/{heartRate}",
            arguments = listOf(navArgument("heartRate") { type = NavType.IntType })
        ) { backStackEntry ->
            val heartRate = backStackEntry.arguments?.getInt("heartRate") ?: 0
            HeartRateResultScreen(
                navController = navController,
                heartRateResult = HeartRateResult(
                    heartRate = heartRate,
                    status = when {
                        heartRate < 60 -> "Low"
                        heartRate > 100 -> "High"
                        else -> "Normal"
                    },
                    timestamp = LocalDateTime.now().toString(),
                    confidenceLevel = "Medium"
                )
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
