package com.example.fakeolx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fakeolx.navigation.Screen
import com.example.fakeolx.ui.viewmodel.AuthViewModel
import com.example.fakeolx.ui.viewmodel.OgloszeniaViewModel
import com.example.fakeolx.ui.viewmodel.UiState

//Ekran moich ogloszen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MojeOgloszeniaScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    ogloszeniaViewModel: OgloszeniaViewModel
) {
    val ogloszenia by ogloszeniaViewModel.ogloszenia.collectAsState()
    val uiState by ogloszeniaViewModel.uiState.collectAsState()
    var listVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        listVisible = true
        ogloszeniaViewModel.loadMojeOgloszenia()
    }

    DisposableEffect(Unit) {
        onDispose {
            ogloszeniaViewModel.loadAllOgloszenia()
        }
    }

    val background = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Moje ogłoszenia") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (uiState as UiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> {
                    if (ogloszenia.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nie masz jeszcze żadnych ogłoszeń")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(ogloszenia, key = { _, item -> item.id }) { index, ogloszenie ->
                                AnimatedVisibility(
                                    visible = listVisible,
                                    enter = fadeIn(
                                        animationSpec = androidx.compose.animation.core.tween(
                                            durationMillis = 280,
                                            delayMillis = (index * 35).coerceAtMost(280)
                                        )
                                    ) + slideInVertically(
                                        initialOffsetY = { it / 4 },
                                        animationSpec = androidx.compose.animation.core.tween(
                                            durationMillis = 280,
                                            delayMillis = (index * 35).coerceAtMost(280)
                                        )
                                    )
                                ) {
                                    OgloszenieCard(
                                        ogloszenie = ogloszenie,
                                        onClick = {
                                            navController.navigate("${Screen.Szczegoly.route}/${ogloszenie.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
