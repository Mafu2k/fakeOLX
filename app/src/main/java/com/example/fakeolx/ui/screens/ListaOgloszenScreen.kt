package com.example.fakeolx.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import com.example.fakeolx.navigation.Screen
import com.example.fakeolx.ui.viewmodel.AuthViewModel
import com.example.fakeolx.ui.viewmodel.OgloszeniaViewModel
import com.example.fakeolx.ui.viewmodel.UiState

//Ekran listy ogloszen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaOgloszenScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    ogloszeniaViewModel: OgloszeniaViewModel
) {
    val ogloszenia by ogloszeniaViewModel.ogloszenia.collectAsState()
    val uiState by ogloszeniaViewModel.uiState.collectAsState()
    val selectedKategoria by ogloszeniaViewModel.selectedKategoria.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showKategoriaDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("fakeOLX") },
                actions = {
                    //Menu
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Menu, "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Moje ogłoszenia") },
                            onClick = {
                                showMenu = false
                                navController.navigate(Screen.MojeOgloszenia.route)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Wyloguj") },
                            onClick = {
                                showMenu = false
                                authViewModel.logout()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.DodajOgloszenie.route) }
            ) {
                Icon(Icons.Default.Add, "Dodaj ogłoszenie")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            //Filtr kategorii
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedKategoria ?: "Wszystkie kategorie",
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    IconButton(onClick = { showKategoriaDialog = true }) {
                        Icon(Icons.Default.FilterList, "Filtruj")
                    }
                    if (selectedKategoria != null) {
                        IconButton(onClick = { ogloszeniaViewModel.filterByKategoria(null) }) {
                            Icon(Icons.Default.Clear, "Wyczyść filtr")
                        }
                    }
                }
            }

            Divider()

            //Lista ogloszen
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
                            Text("Brak ogłoszeń")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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

    //Dialog wyboru kategorii
    if (showKategoriaDialog) {
        AlertDialog(
            onDismissRequest = { showKategoriaDialog = false },
            title = { Text("Wybierz kategorię") },
            text = {
                Column {
                    Kategoria.getAllKategorie().forEach { kategoria ->
                        TextButton(
                            onClick = {
                                ogloszeniaViewModel.filterByKategoria(kategoria)
                                showKategoriaDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(kategoria)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showKategoriaDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

//Karta ogloszenia
@Composable
fun OgloszenieCard(
    ogloszenie: Ogloszenie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = ogloszenie.tytul,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ogloszenie.tresc,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${ogloszenie.cena} zł",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = ogloszenie.miasto,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = ogloszenie.kategoria,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
