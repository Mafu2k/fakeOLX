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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.ui.viewmodel.OgloszeniaViewModel
import com.example.fakeolx.ui.viewmodel.UiState

//Ekran edycji ogloszenia
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdytujOgloszenieScreen(
    navController: NavController,
    ogloszenieId: String,
    ogloszeniaViewModel: OgloszeniaViewModel
) {
    val ogloszenie by ogloszeniaViewModel.selectedOgloszenie.collectAsState()
    val uiState by ogloszeniaViewModel.uiState.collectAsState()

    var tytul by remember { mutableStateOf("") }
    var tresc by remember { mutableStateOf("") }
    var cena by remember { mutableStateOf("") }
    var miasto by remember { mutableStateOf("") }
    var selectedKategoria by remember { mutableStateOf(Kategoria.INNE.displayName) }
    var expanded by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(ogloszenieId) {
        ogloszeniaViewModel.selectOgloszenie(ogloszenieId)
    }

    LaunchedEffect(ogloszenie) {
        ogloszenie?.let {
            if (!isLoaded) {
                tytul = it.tytul
                tresc = it.tresc
                cena = it.cena.toString()
                miasto = it.miasto
                selectedKategoria = it.kategoria
                isLoaded = true
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success && isLoaded) {
            ogloszeniaViewModel.resetUiState()
            navController.navigateUp()
        }
    }

    val background = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Edytuj ogłoszenie") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
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
            if (!isLoaded) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Zaktualizuj szczegóły i odśwież ogłoszenie.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
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
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                OutlinedTextField(
                                    value = tytul,
                                    onValueChange = { tytul = it },
                                    label = { Text("Tytuł") },
                                    leadingIcon = { Icon(Icons.Default.Edit, null) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = selectedKategoria,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Kategoria") },
                                        leadingIcon = { Icon(Icons.Default.LocalOffer, null) },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        Kategoria.getAllKategorie().forEach { kategoria ->
                                            DropdownMenuItem(
                                                text = { Text(kategoria) },
                                                onClick = {
                                                    selectedKategoria = kategoria
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = cena,
                                    onValueChange = { cena = it },
                                    label = { Text("Cena (zł)") },
                                    leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = miasto,
                                    onValueChange = { miasto = it },
                                    label = { Text("Miasto") },
                                    leadingIcon = { Icon(Icons.Default.Place, null) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = tresc,
                                    onValueChange = { tresc = it },
                                    label = { Text("Opis") },
                                    leadingIcon = { Icon(Icons.Default.Description, null) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    maxLines = 8
                                )

                                if (uiState is UiState.Error) {
                                    Text(
                                        text = (uiState as UiState.Error).message,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Button(
                                    onClick = {
                                        val cenaDouble = cena.toDoubleOrNull() ?: 0.0
                                        ogloszeniaViewModel.updateOgloszenie(
                                            id = ogloszenieId,
                                            tytul = tytul,
                                            tresc = tresc,
                                            kategoria = selectedKategoria,
                                            cena = cenaDouble,
                                            miasto = miasto,
                                            zdjecieUrl = ogloszenie?.zdjecieUrl ?: ""
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    enabled = uiState !is UiState.Loading
                                ) {
                                    if (uiState is UiState.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Zapisz zmiany")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
