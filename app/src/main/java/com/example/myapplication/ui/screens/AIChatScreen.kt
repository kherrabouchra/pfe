package com.example.chat

import android.net.Uri
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.utils.UiState
import com.example.myapplication.viewmodel.AIViewModel
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    aiViewModel: AIViewModel = viewModel(),
    navController: NavController
) {
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by aiViewModel.uiState.collectAsState()
    var messages = remember { mutableStateListOf<Pair<String, String>>() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("Dashboard") }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
            Text(
                text = "AI Assistant",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Message List
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            reverseLayout = false // Newest messages at the bottom
        ) {
            items(messages) { message ->
                val alignment = if (message.first == "User") Alignment.End else Alignment.Start
                Column(
                    horizontalAlignment = alignment,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.align(alignment)
                            .background(

                                color = if (message.first == "User") Color(0xFF0031E2) else Color(0xFFD3D3D3), // Light gray for AI messages
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = message.second,
                            color = if (message.first == "User")  Color.White else Color.Black
                        )
                    }
                    Text(
                        text = getDynamicTime(), // Dynamic timestamp
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Loading Indicator
        if (uiState is UiState.Loading && messages.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Input Area
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(56.dp), // Set fixed height for input area
            verticalAlignment = Alignment.CenterVertically
        ) {
            var userInput by rememberSaveable { mutableStateOf("") }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .background(Color.Gray, shape = MaterialTheme.shapes.medium)
            ) {
                TextField(

                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = { Text(text = stringResource(R.string.label_prompt)) },
                    colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,

                    ),

                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    modifier = Modifier.align(AbsoluteAlignment.CenterRight),
                    onClick = { /* TODO: Handle notifications */ }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = "upload image"
                        ,modifier = Modifier.size(24.dp),

                    )
                }
            }

            Button(
                onClick = {
                    messages.add("User" to userInput)
                    aiViewModel.sendMessage(userInput) { response ->
                        messages.add("AI" to response)
                    }
                    userInput = "" // Clear input after sending
                },
                enabled = userInput.isNotEmpty(),

            ) {
                Text(text = stringResource(R.string.action_send))
            }
        }

    }
}

// Function to get dynamic time
@Composable
fun getDynamicTime(): String {
    // Replace with actual logic to calculate time
    return "Just now" // Placeholder for dynamic time logic
}

@Preview(showSystemUi = true)
@Composable
fun AIChatScreenPreview() {
    // Create a mock NavController for preview purposes
    val navController = rememberNavController() // Use a mock NavController
    AIChatScreen(navController = navController) // Pass the navController
}
