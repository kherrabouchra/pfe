package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.home), "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { onNavigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.vitals), "Vitals") },
            label = { Text("Vitals") },
            selected = currentRoute == "vitals",
            onClick = { onNavigate("vitals") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.meds), "Meds") },
            label = { Text("Meds") },
            selected = currentRoute == "meds",
            onClick = { onNavigate("meds") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ChatBubbleOutline, "Assistant") },
            label = { Text("AI Assistant") },
            selected = currentRoute == "assistant",
            onClick = { onNavigate("chat") }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.settings), "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == "settings",
            onClick = { onNavigate("settings") }
        )
    }
}

