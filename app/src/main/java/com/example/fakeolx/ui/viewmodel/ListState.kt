package com.example.fakeolx.ui.viewmodel

import com.example.fakeolx.data.model.Ogloszenie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface ListState {
    data object Loading : ListState
    data class Loaded(val ogloszenia: List<Ogloszenie>) : ListState
    data class Error(val message: String) : ListState
}

fun Flow<List<Ogloszenie>>.toListState(scope: CoroutineScope): StateFlow<ListState> =
    map<List<Ogloszenie>, ListState> { ListState.Loaded(it) }
        .catch { emit(ListState.Error(it.toUserMessage())) }
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), ListState.Loading)
