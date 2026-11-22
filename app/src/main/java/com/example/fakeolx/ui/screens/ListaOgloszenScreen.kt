package com.example.fakeolx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.ui.components.OgloszeniaList
import com.example.fakeolx.ui.components.appBackground
import com.example.fakeolx.ui.components.fakeOlxWordmark
import com.example.fakeolx.ui.components.icon
import com.example.fakeolx.ui.viewmodel.ListaViewModel

private val quickKategorie = listOf(
    Kategoria.PRACA to "Oferty od zaraz",
    Kategoria.KOREPETYCJE to "Nauczyciele i kursy",
    Kategoria.ELEKTRONIKA to "Telefony, laptopy, RTV"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaOgloszenScreen(
    viewModel: ListaViewModel,
    onOgloszenieClick: (String) -> Unit,
    onAddClick: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedKategoria by viewModel.kategoria.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(fakeOlxWordmark(), style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Dodaj ogłoszenie") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(appBackground())
                .padding(padding)
        ) {
            OgloszeniaList(
                state = state,
                emptyMessage = if (query.isBlank()) {
                    "W tej kategorii nie ma jeszcze ogłoszeń"
                } else {
                    "Nic nie znaleziono dla „$query”"
                },
                emptyIcon = Icons.Default.SearchOff,
                onOgloszenieClick = onOgloszenieClick
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Czego dziś szukasz?", style = MaterialTheme.typography.headlineMedium)
                        SearchField(query = query, onQueryChange = viewModel::updateQuery)
                    }
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(quickKategorie) { (kategoria, subtitle) ->
                            CategoryQuickCard(
                                kategoria = kategoria,
                                subtitle = subtitle,
                                onClick = { viewModel.selectKategoria(kategoria) }
                            )
                        }
                    }
                }
                item {
                    KategoriaChips(
                        selected = selectedKategoria,
                        onSelect = viewModel::selectKategoria
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Szukaj po tytule, opisie lub mieście") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Wyczyść wyszukiwanie")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun KategoriaChips(
    selected: Kategoria?,
    onSelect: (Kategoria?) -> Unit
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val rowState = rememberLazyListState()
    LaunchedEffect(selected) {
        val selectedIndex = selected?.let { it.ordinal + 1 } ?: 0
        rowState.animateScrollToItem(selectedIndex)
    }
    LazyRow(
        state = rowState,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text("Wszystko") },
                colors = chipColors
            )
        }
        items(Kategoria.entries) { kategoria ->
            FilterChip(
                selected = selected == kategoria,
                onClick = { onSelect(kategoria) },
                label = { Text(kategoria.displayName) },
                colors = chipColors
            )
        }
    }
}

@Composable
private fun CategoryQuickCard(
    kategoria: Kategoria,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        shadowElevation = 6.dp,
        modifier = Modifier
            .width(172.dp)
            .height(96.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                    )
                )
                .padding(12.dp)
        ) {
            Icon(
                imageVector = kategoria.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = kategoria.displayName,
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
