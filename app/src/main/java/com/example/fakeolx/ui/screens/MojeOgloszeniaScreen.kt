package com.example.fakeolx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fakeolx.ui.components.OgloszeniaList
import com.example.fakeolx.ui.components.appBackground
import com.example.fakeolx.ui.viewmodel.ListState
import com.example.fakeolx.ui.viewmodel.MojeOgloszeniaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MojeOgloszeniaScreen(
    viewModel: MojeOgloszeniaViewModel,
    onOgloszenieClick: (String) -> Unit,
    onAddClick: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val current = state) {
                            is ListState.Loaded -> "Moje ogłoszenia (${current.ogloszenia.size})"
                            else -> "Moje ogłoszenia"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                emptyMessage = "Nie masz jeszcze ogłoszeń. Dodaj pierwsze przyciskiem na dole ekranu.",
                emptyIcon = Icons.Default.Inventory2,
                onOgloszenieClick = onOgloszenieClick
            ) {
                item {
                    Text(
                        text = "Kliknij ogłoszenie, aby je edytować albo usunąć.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
