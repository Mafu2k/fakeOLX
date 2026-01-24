package com.example.fakeolx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    LaunchedEffect(Unit) {
        ogloszeniaViewModel.loadMojeOgloszenia()
    }

    DisposableEffect(Unit) {
        onDispose {
            ogloszeniaViewModel.loadAllOgloszenia()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje ogłoszenia") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nie masz jeszcze żadnych ogłoszeń")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ogloszenia) { ogloszenie ->
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
