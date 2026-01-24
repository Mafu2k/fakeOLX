package com.example.fakeolx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import java.text.SimpleDateFormat
import java.util.*

//Ekran szczegolowy ogloszenia
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SzczegolyScreen(
    navController: NavController,
    ogloszenieId: String,
    authViewModel: AuthViewModel,
    ogloszeniaViewModel: OgloszeniaViewModel
) {
    val ogloszenie by ogloszeniaViewModel.selectedOgloszenie.collectAsState()
    val uiState by ogloszeniaViewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(ogloszenieId) {
        ogloszeniaViewModel.selectOgloszenie(ogloszenieId)
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success && ogloszenie == null) {
            navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły ogłoszenia") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                },
                actions = {
                    //Edycja i usuniecie dla autora
                    if (ogloszenie?.autorId == currentUser?.uid) {
                        IconButton(
                            onClick = {
                                navController.navigate("${Screen.EdytujOgloszenie.route}/$ogloszenieId")
                            }
                        ) {
                            Icon(Icons.Default.Edit, "Edytuj")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Usuń")
                        }
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
                ogloszenie?.let { ogl ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        //Tytul
                        Text(
                            text = ogl.tytul,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        //Cena
                        Text(
                            text = "${ogl.cena} zł",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        //Kategoria
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Kategoria:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(text = ogl.kategoria)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        //Miasto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Miasto:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(text = ogl.miasto)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        //Data
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Data dodania:",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                    .format(ogl.dataUtworzenia.toDate())
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        //Opis
                        Text(
                            text = "Opis",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ogl.tresc,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))

                        //Kontakt
                        Text(
                            text = "Kontakt",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = ogl.autorEmail,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    //Dialog usuwania
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Usuń ogłoszenie") },
            text = { Text("Czy na pewno chcesz usunąć to ogłoszenie?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        ogloszeniaViewModel.deleteOgloszenie(ogloszenieId)
                        showDeleteDialog = false
                        navController.navigateUp()
                    }
                ) {
                    Text("Usuń")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}
