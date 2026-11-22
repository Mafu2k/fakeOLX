package com.example.fakeolx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fakeolx.BuildConfig
import com.example.fakeolx.ui.components.appBackground
import com.example.fakeolx.ui.components.formatMonthYear
import com.example.fakeolx.ui.viewmodel.ProfilUiState
import com.example.fakeolx.ui.viewmodel.ProfilViewModel

private enum class ProfilDialog { ZMIANA_NAZWY, RESET_HASLA, O_APLIKACJI, WYLOGOWANIE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    viewModel: ProfilViewModel,
    onLogout: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var openDialog by rememberSaveable { mutableStateOf<ProfilDialog?>(null) }

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
                title = { Text("Profil") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(appBackground())
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfilHeader(state)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(value = state.liczbaOgloszen, label = "Moje ogłoszenia", modifier = Modifier.weight(1f))
                StatCard(value = state.liczbaUlubionych, label = "Ulubione", modifier = Modifier.weight(1f))
            }

            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfilOption(
                        icon = Icons.Default.Badge,
                        title = "Zmień nazwę wyświetlaną",
                        subtitle = state.displayName,
                        onClick = { openDialog = ProfilDialog.ZMIANA_NAZWY }
                    )
                    HorizontalDivider()
                    ProfilOption(
                        icon = Icons.Default.Key,
                        title = "Zmień hasło",
                        subtitle = "Wyślemy link na Twój adres e-mail",
                        onClick = { openDialog = ProfilDialog.RESET_HASLA }
                    )
                    HorizontalDivider()
                    ProfilOption(
                        icon = Icons.Default.Info,
                        title = "O aplikacji",
                        subtitle = "Wersja ${BuildConfig.VERSION_NAME}",
                        onClick = { openDialog = ProfilDialog.O_APLIKACJI }
                    )
                    HorizontalDivider()
                    ProfilOption(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Wyloguj się",
                        subtitle = null,
                        contentColor = MaterialTheme.colorScheme.error,
                        onClick = { openDialog = ProfilDialog.WYLOGOWANIE }
                    )
                }
            }
        }
    }

    when (openDialog) {
        ProfilDialog.ZMIANA_NAZWY -> ChangeNameDialog(
            currentName = state.displayName,
            onConfirm = {
                viewModel.updateDisplayName(it)
                openDialog = null
            },
            onDismiss = { openDialog = null }
        )
        ProfilDialog.RESET_HASLA -> ConfirmDialog(
            title = "Zmiana hasła",
            text = "Wyślemy wiadomość z linkiem do ustawienia nowego hasła na adres ${state.email}.",
            confirmLabel = "Wyślij link",
            onConfirm = {
                viewModel.sendPasswordReset()
                openDialog = null
            },
            onDismiss = { openDialog = null }
        )
        ProfilDialog.O_APLIKACJI -> AlertDialog(
            onDismissRequest = { openDialog = null },
            title = { Text("fakeOLX ${BuildConfig.VERSION_NAME}") },
            text = {
                Text("Aplikacja z ogłoszeniami lokalnymi: sprzedaż rzeczy, oferty pracy i korepetycje.")
            },
            confirmButton = {
                TextButton(onClick = { openDialog = null }) { Text("Zamknij") }
            }
        )
        ProfilDialog.WYLOGOWANIE -> ConfirmDialog(
            title = "Wylogować się?",
            text = "Aby znowu dodawać ogłoszenia, trzeba będzie zalogować się ponownie.",
            confirmLabel = "Wyloguj",
            onConfirm = {
                openDialog = null
                onLogout()
            },
            onDismiss = { openDialog = null }
        )
        null -> Unit
    }
}

@Composable
private fun ProfilHeader(state: ProfilUiState) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.displayName.take(1).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = state.displayName, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = state.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            state.memberSinceMillis?.let {
                Text(
                    text = "Na fakeOLX od ${formatMonthYear(it)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    value: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfilOption(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            headlineColor = contentColor,
            leadingIconColor = contentColor
        ),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun ChangeNameDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nazwa wyświetlana") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nowa nazwa") },
                supportingText = { Text("Widoczna w Twoim profilu") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) { Text("Zapisz") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}

@Composable
private fun ConfirmDialog(
    title: String,
    text: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        }
    )
}
