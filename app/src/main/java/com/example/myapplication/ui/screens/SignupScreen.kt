package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.R

@Composable
fun SignupScreen(onSignup: () -> Unit, onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize().fillMaxHeight()
            .padding(26.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.better_logo),
                contentDescription = "logo",
                modifier = Modifier.size(140.dp)
            )

            Text(
                text = "Your wellness all",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Text(
                text = "in one place!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
        }

        Spacer(modifier = Modifier.height(120.dp))
        Column{
            OutlinedButton(
                onClick = onSignup, // This will navigate to the Questionnaire screen
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Continue with Google")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onSignup,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Continue with Facebook")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(110.dp)
                )
                Text(
                    text = "or",
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.Gray
                )
                Divider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(110.dp)
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = Color.Black,
                )
                TextButton(
                    onClick = onLoginClick
                ) {
                    Text(
                        text = "Login",
                        color = Color.Blue
                    )
                }
            }
        }




    }
}

@Preview(showSystemUi = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        onSignup = {},
        onLoginClick = {}
    )
}