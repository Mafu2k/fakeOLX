package com.example.fakeolx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.navigation.Screen
import com.example.fakeolx.ui.components.InfoRow
import com.example.fakeolx.ui.components.PricePill
import com.example.fakeolx.ui.components.formatPrice
import com.example.fakeolx.ui.viewmodel.AuthViewModel
import com.example.fakeolx.ui.viewmodel.OgloszeniaViewModel
import com.example.fakeolx.ui.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.Locale

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
                title = { Text("Szczegóły ogłoszenia") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                },
                actions = {
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
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
                    ogloszenie?.let { ogl ->
                        val miastoLabel = ogl.miasto.ifBlank { "Online" }
                        val isPraca = ogl.kategoria == Kategoria.PRACA.displayName
                        val isKorepetycje = ogl.kategoria == Kategoria.KOREPETYCJE.displayName
                        val specjalizacjaLabel = if (isPraca) "Stanowisko" else "Przedmiot"
                        val trybLabel = if (isPraca) "Tryb pracy" else "Tryb zajęć"
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.large,
                                tonalElevation = 1.dp,
                                shadowElevation = 6.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.BottomStart
                                ) {
                                    if (ogl.zdjecieUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = ogl.zdjecieUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            MaterialTheme.colorScheme.surfaceVariant,
                                                            MaterialTheme.colorScheme.surface
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    PricePill(
                                        text = formatPrice(ogl.cena),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }

                            Text(
                                text = ogl.tytul,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = miastoLabel,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Surface(
                                shape = MaterialTheme.shapes.large,
                                tonalElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    InfoRow(
                                        icon = Icons.Default.Label,
                                        label = "Kategoria",
                                        value = ogl.kategoria
                                    )
                                    if ((isPraca || isKorepetycje) && ogl.specjalizacja.isNotBlank()) {
                                        InfoRow(
                                            icon = if (isPraca) Icons.Default.Work else Icons.Default.School,
                                            label = specjalizacjaLabel,
                                            value = ogl.specjalizacja
                                        )
                                    }
                                    if ((isPraca || isKorepetycje) && ogl.tryb.isNotBlank()) {
                                        InfoRow(
                                            icon = Icons.Default.Tune,
                                            label = trybLabel,
                                            value = ogl.tryb
                                        )
                                    }
                                    InfoRow(
                                        icon = Icons.Default.Place,
                                        label = "Miasto",
                                        value = miastoLabel
                                    )
                                    InfoRow(
                                        icon = Icons.Default.CalendarToday,
                                        label = "Data dodania",
                                        value = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                                            .format(ogl.dataUtworzenia.toDate())
                                    )
                                }
                            }

                            Surface(
                                shape = MaterialTheme.shapes.large,
                                tonalElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Opis",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = ogl.tresc,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }

                            Surface(
                                shape = MaterialTheme.shapes.large,
                                tonalElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Kontakt",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = ogl.autorEmail,
                                            style = MaterialTheme.typography.bodyMedium
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
