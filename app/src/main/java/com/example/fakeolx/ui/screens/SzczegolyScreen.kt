package com.example.fakeolx.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import com.example.fakeolx.ui.components.CenteredMessage
import com.example.fakeolx.ui.components.InfoRow
import com.example.fakeolx.ui.components.LoadingIndicator
import com.example.fakeolx.ui.components.PricePill
import com.example.fakeolx.ui.components.appBackground
import com.example.fakeolx.ui.components.formatDate
import com.example.fakeolx.ui.components.priceLabel
import com.example.fakeolx.ui.components.specjalizacjaLabel
import com.example.fakeolx.ui.components.trybLabel
import com.example.fakeolx.ui.viewmodel.SzczegolyState
import com.example.fakeolx.ui.viewmodel.SzczegolyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SzczegolyScreen(
    viewModel: SzczegolyViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isUlubione by viewModel.isUlubione.collectAsStateWithLifecycle()
    val isDeleted by viewModel.isDeleted.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val loaded = state as? SzczegolyState.Loaded

    LaunchedEffect(isDeleted) {
        if (isDeleted) onDeleted()
    }
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.messageShown()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły ogłoszenia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
                actions = {
                    when {
                        loaded == null -> Unit
                        loaded.isAuthor -> {
                            IconButton(onClick = { onEdit(loaded.ogloszenie.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edytuj ogłoszenie")
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Usuń ogłoszenie")
                            }
                        }
                        else -> IconButton(onClick = viewModel::toggleUlubione) {
                            Icon(
                                imageVector = if (isUlubione) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isUlubione) "Usuń z ulubionych" else "Dodaj do ulubionych",
                                tint = if (isUlubione) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            if (loaded != null && !loaded.isAuthor) {
                ContactBar(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO, "mailto:${loaded.ogloszenie.autorEmail}".toUri())
                            .putExtra(Intent.EXTRA_SUBJECT, "fakeOLX: ${loaded.ogloszenie.tytul}")
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            viewModel.showMessage("Brak aplikacji pocztowej na telefonie")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(appBackground())
                .padding(padding)
        ) {
            when (val current = state) {
                SzczegolyState.Loading -> LoadingIndicator()
                SzczegolyState.NotFound -> CenteredMessage(
                    text = "To ogłoszenie zostało usunięte",
                    icon = Icons.Default.SearchOff,
                    modifier = Modifier.align(Alignment.Center)
                )
                is SzczegolyState.Error -> CenteredMessage(
                    text = current.message,
                    icon = Icons.Default.CloudOff,
                    modifier = Modifier.align(Alignment.Center)
                )
                is SzczegolyState.Loaded -> OgloszenieDetails(current.ogloszenie)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Usunąć ogłoszenie?") },
            text = { Text("Ogłoszenie zniknie z listy i z ulubionych innych osób. Tej operacji nie można cofnąć.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.delete()
                    }
                ) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
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

@Composable
private fun OgloszenieDetails(ogloszenie: Ogloszenie) {
    val kategoria = Kategoria.fromDisplayName(ogloszenie.kategoria)
    val miasto = ogloszenie.miasto.ifBlank { "Online" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Box(contentAlignment = Alignment.BottomStart) {
                if (ogloszenie.zdjecieUrl.isNotBlank()) {
                    AsyncImage(
                        model = ogloszenie.zdjecieUrl,
                        contentDescription = "Zdjęcie: ${ogloszenie.tytul}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)
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
                PricePill(text = ogloszenie.priceLabel(), modifier = Modifier.padding(12.dp))
            }
        }

        Text(text = ogloszenie.tytul, style = MaterialTheme.typography.headlineMedium)

        DetailsSection(title = "Informacje") {
            InfoRow(icon = Icons.AutoMirrored.Filled.Label, label = "Kategoria", value = ogloszenie.kategoria)
            if (kategoria.wymagaSzczegolow && ogloszenie.specjalizacja.isNotBlank()) {
                InfoRow(
                    icon = if (kategoria == Kategoria.PRACA) Icons.Default.Work else Icons.Default.School,
                    label = kategoria.specjalizacjaLabel,
                    value = ogloszenie.specjalizacja
                )
            }
            if (kategoria.wymagaSzczegolow && ogloszenie.tryb.isNotBlank()) {
                InfoRow(icon = Icons.Default.Tune, label = kategoria.trybLabel, value = ogloszenie.tryb)
            }
            InfoRow(icon = Icons.Default.Place, label = "Miasto", value = miasto)
            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = "Dodano",
                value = formatDate(ogloszenie.dataUtworzenia)
            )
        }

        DetailsSection(title = "Opis") {
            Text(text = ogloszenie.tresc, style = MaterialTheme.typography.bodyLarge)
        }

        DetailsSection(title = "Kontakt") {
            InfoRow(icon = Icons.Default.Email, label = "E-mail", value = ogloszenie.autorEmail)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DetailsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun ContactBar(onClick: () -> Unit) {
    Surface(tonalElevation = 3.dp) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(52.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Napisz do sprzedającego")
            }
        }
    }
}
