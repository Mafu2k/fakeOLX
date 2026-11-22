package com.example.fakeolx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fakeolx.ui.components.CenteredMessage
import com.example.fakeolx.ui.components.LoadingIndicator
import com.example.fakeolx.ui.components.OgloszenieFormContent
import com.example.fakeolx.ui.components.appBackground
import com.example.fakeolx.ui.viewmodel.OgloszenieFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OgloszenieFormScreen(
    viewModel: OgloszenieFormViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val title = if (viewModel.isEditMode) "Edytuj ogłoszenie" else "Dodaj ogłoszenie"
    val subtitle = if (viewModel.isEditMode) {
        "Zmień dane i zapisz. Data dodania ogłoszenia się nie zmieni."
    } else {
        "Uzupełnij dane przedmiotu lub oferty. Ogłoszenie od razu pojawi się na liście."
    }
    val submitLabel = if (viewModel.isEditMode) "Zapisz zmiany" else "Opublikuj ogłoszenie"

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Anuluj i wróć")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(appBackground())
                .padding(padding)
        ) {
            val loadError = state.loadError
            when {
                state.isLoading -> LoadingIndicator()
                loadError != null -> CenteredMessage(
                    text = loadError,
                    icon = Icons.Default.CloudOff,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = 1.dp,
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OgloszenieFormContent(
                                form = state.form,
                                errors = state.errors,
                                onFormChange = viewModel::onFormChange
                            )
                            state.saveError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error)
                            }
                            Button(
                                onClick = viewModel::save,
                                enabled = !state.isSaving,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                if (state.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(submitLabel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
