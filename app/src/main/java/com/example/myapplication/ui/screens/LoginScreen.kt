package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.myapplication.R
import com.example.myapplication.ui.components.BetterButton
import com.example.myapplication.ui.components.BetterTextField
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.Start)
        )
        
       TextField(
            value = email,
           colors = TextFieldDefaults.colors(
               unfocusedContainerColor = Color.Transparent,
               focusedContainerColor = Color.Transparent,
           ),
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        TextField(
            value = password,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
            ),
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Login"

            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Trouble logging in?",
            fontSize = 14.sp,
            color = Color.Blue,
            modifier = Modifier.clickable { /* Handle trouble logging in */ }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLogin = {}
    )
} 