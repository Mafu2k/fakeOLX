package com.example.fakeolx.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import com.example.fakeolx.navigation.Screen
import com.example.fakeolx.ui.components.PricePill
import com.example.fakeolx.ui.components.formatPrice
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
    var query by remember { mutableStateOf("") }
    var listVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        listVisible = true
    }

    val filteredOgloszenia = remember(ogloszenia, query) {
        if (query.isBlank()) {
            ogloszenia
        } else {
            ogloszenia.filter {
                it.tytul.contains(query, ignoreCase = true) ||
                    it.tresc.contains(query, ignoreCase = true) ||
                    it.miasto.contains(query, ignoreCase = true) ||
                    it.specjalizacja.contains(query, ignoreCase = true) ||
                    it.tryb.contains(query, ignoreCase = true)
            }
        }
    }

    val background = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
            MaterialTheme.colorScheme.background
        )
    )
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append("fake")
                            }
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("OLX")
                            }
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
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
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.DodajOgloszenie.route) },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Dodaj") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Czego dziś szukasz?",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Szukaj ogłoszeń, np. rower") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, "Wyczyść")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CategoryQuickCard(
                            label = "Praca",
                            subtitle = "Oferty od zaraz",
                            icon = Icons.Default.Work,
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            onClick = { ogloszeniaViewModel.filterByKategoria(Kategoria.PRACA.displayName) }
                        )
                    }
                    item {
                        CategoryQuickCard(
                            label = "Korepetycje",
                            subtitle = "Nauczyciele i kursy",
                            icon = Icons.Default.School,
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            onClick = { ogloszeniaViewModel.filterByKategoria(Kategoria.KOREPETYCJE.displayName) }
                        )
                    }
                    item {
                        CategoryQuickCard(
                            label = "Elektronika",
                            subtitle = "Gadżety w okolicy",
                            icon = Icons.Default.PhoneAndroid,
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            ),
                            onClick = { ogloszeniaViewModel.filterByKategoria(Kategoria.ELEKTRONIKA.displayName) }
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedKategoria == null,
                            onClick = { ogloszeniaViewModel.filterByKategoria(null) },
                            label = { Text("Wszystko") },
                            colors = chipColors
                        )
                    }
                    itemsIndexed(Kategoria.getAllKategorie()) { _, kategoria ->
                        FilterChip(
                            selected = selectedKategoria == kategoria,
                            onClick = { ogloszeniaViewModel.filterByKategoria(kategoria) },
                            label = { Text(kategoria) },
                            colors = chipColors
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

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
                    if (filteredOgloszenia.isEmpty()) {
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(filteredOgloszenia, key = { _, item -> item.id }) { index, ogloszenie ->
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

//Karta ogloszenia
@Composable
fun OgloszenieCard(
    ogloszenie: Ogloszenie,
    onClick: () -> Unit
) {
    val priceText = remember(ogloszenie.cena) { formatPrice(ogloszenie.cena) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (ogloszenie.zdjecieUrl.isNotBlank()) {
                    AsyncImage(
                        model = ogloszenie.zdjecieUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clip(RoundedCornerShape(18.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ogloszenie.tytul,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    PricePill(text = priceText)
                }

                Text(
                    text = ogloszenie.tresc,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (ogloszenie.specjalizacja.isNotBlank() || ogloszenie.tryb.isNotBlank()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (ogloszenie.specjalizacja.isNotBlank()) {
                            MiniChip(
                                text = ogloszenie.specjalizacja,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        if (ogloszenie.tryb.isNotBlank()) {
                            MiniChip(
                                text = ogloszenie.tryb,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = ogloszenie.miasto.ifBlank { "Online" },
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text(
                        text = ogloszenie.kategoria,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryQuickCard(
    label: String,
    subtitle: String,
    icon: ImageVector,
    colors: List<Color>,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        shadowElevation = 6.dp,
        modifier = Modifier
            .width(172.dp)
            .height(96.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors))
                .padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun MiniChip(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
