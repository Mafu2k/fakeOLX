package com.example.fakeolx.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.ui.viewmodel.OgloszeniaViewModel
import com.example.fakeolx.ui.viewmodel.UiState

//Ekran dodawania ogloszenia
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DodajOgloszenieScreen(
    navController: NavController,
    ogloszeniaViewModel: OgloszeniaViewModel
) {
    var tytul by remember { mutableStateOf("") }
    var tresc by remember { mutableStateOf("") }
    var cena by remember { mutableStateOf("") }
    var miasto by remember { mutableStateOf("") }
    var selectedKategoria by remember { mutableStateOf(Kategoria.INNE.displayName) }
    var expanded by remember { mutableStateOf(false) }

    val uiState by ogloszeniaViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            ogloszeniaViewModel.resetUiState()
            navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj ogłoszenie") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Tytul
            OutlinedTextField(
                value = tytul,
                onValueChange = { tytul = it },
                label = { Text("Tytuł") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            //Kategoria
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedKategoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategoria") },
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

            //Cena
            OutlinedTextField(
                value = cena,
                onValueChange = { cena = it },
                label = { Text("Cena (zł)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            //Miasto
            OutlinedTextField(
                value = miasto,
                onValueChange = { miasto = it },
                label = { Text("Miasto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            //Opis
            OutlinedTextField(
                value = tresc,
                onValueChange = { tresc = it },
                label = { Text("Opis") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )

            //Komunikat bledu
            if (uiState is UiState.Error) {
                Text(
                    text = (uiState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            //Przycisk dodaj
            Button(
                onClick = {
                    val cenaDouble = cena.toDoubleOrNull() ?: 0.0
                    ogloszeniaViewModel.addOgloszenie(
                        tytul = tytul,
                        tresc = tresc,
                        kategoria = selectedKategoria,
                        cena = cenaDouble,
                        miasto = miasto
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is UiState.Loading
            ) {
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Dodaj ogłoszenie")
                }
            }
        }
    }
}
