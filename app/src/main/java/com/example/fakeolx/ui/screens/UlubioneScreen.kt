package com.example.fakeolx.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.fakeolx.ui.viewmodel.UlubioneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UlubioneScreen(
    viewModel: UlubioneViewModel,
    onOgloszenieClick: (String) -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Ulubione") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                emptyMessage = "Nie masz jeszcze ulubionych. Dotknij serca w szczegółach ogłoszenia, aby je tu zapisać.",
                emptyIcon = Icons.Outlined.FavoriteBorder,
                onOgloszenieClick = onOgloszenieClick
            ) {
                item {
                    Text(
                        text = "Zapisane ogłoszenia, do których możesz wrócić później.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
