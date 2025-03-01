package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.navigation.Screen
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    navController: NavController,
    onRouteChange: (String) -> Unit
) {
    TabRow(
        selectedTabIndex = getSelectedIndex(currentRoute),
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            if (getSelectedIndex(currentRoute) < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[getSelectedIndex(currentRoute)])
                        .offset(y = (-68).dp),
                    height = 6.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        val items = listOf(
            Triple("Home", R.drawable.ic_home, "home"),
            Triple("Activities", R.drawable.ic_activities, "activities"),
            Triple("AI Assistant",  R.drawable.ic_chat, "AIChat"),
            Triple("Settings", R.drawable.ic_settings, "settings")
        )

        items.forEachIndexed { index, (label, icon, route) ->
            Tab(
                selected = currentRoute == route,
                onClick = {
                    when (route) {
                        "AIChat" -> navController.navigate(Screen.AIChat.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                        "activities" -> navController.navigate(Screen.Activities.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                        "settings" -> navController.navigate(Screen.Settings.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                        "home" -> navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                        else -> onRouteChange(route)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.background(
                    if (currentRoute == route)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0f)
                    else Color.Unspecified
                )
            )
        }
    }
}

private fun getSelectedIndex(currentRoute: String): Int {
    return when (currentRoute) {
        "home" -> 0
        "activities" -> 1
        "AIChat" -> 2
        "settings" -> 3
        else -> 0
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    BottomNavigationBar(
        currentRoute = "home",
        navController = navController,
        onRouteChange = {}
    )
}

