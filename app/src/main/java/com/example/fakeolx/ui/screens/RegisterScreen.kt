package com.example.fakeolx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fakeolx.navigation.Screen
import com.example.fakeolx.ui.components.FakeOLXLogo
import com.example.fakeolx.ui.viewmodel.AuthState
import com.example.fakeolx.ui.viewmodel.AuthViewModel

//Ekran rejestracji
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showContent by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        showContent = true
    }

    //Przekierowanie po rejestracji
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate(Screen.Lista.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    val background = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(containerColor = Color.Transparent) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rejestracja",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                FakeOLXLogo(tagline = "Wskocz do świata ogłoszeń.")

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = 1.dp,
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                leadingIcon = { Icon(Icons.Default.Email, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Hasło") },
                                leadingIcon = { Icon(Icons.Default.Lock, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation()
                            )

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Powtórz hasło") },
                                leadingIcon = { Icon(Icons.Default.Lock, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation()
                            )

                            if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
                                Text(
                                    text = "Hasła nie są zgodne",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (authState is AuthState.Error) {
                                Text(
                                    text = (authState as AuthState.Error).message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Button(
                                onClick = {
                                    if (password == confirmPassword) {
                                        authViewModel.register(email, password)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                enabled = authState !is AuthState.Loading && password == confirmPassword
                            ) {
                                if (authState is AuthState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Zarejestruj się")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
