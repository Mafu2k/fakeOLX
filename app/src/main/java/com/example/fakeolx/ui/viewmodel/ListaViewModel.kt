package com.example.fakeolx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import com.example.fakeolx.data.repository.OgloszeniaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModel : ViewModel() {
    private val repository = OgloszeniaRepository()

    private val _kategoria = MutableStateFlow<Kategoria?>(null)
    val kategoria: StateFlow<Kategoria?> = _kategoria.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val state: StateFlow<ListState> = _kategoria
        .flatMapLatest { kategoria ->
            if (kategoria == null) repository.observeAll() else repository.observeByKategoria(kategoria)
        }
        .combine(_query) { ogloszenia, query -> ogloszenia.filter { it.matchesQuery(query) } }
        .toListState(viewModelScope)

    fun selectKategoria(kategoria: Kategoria?) {
        _kategoria.value = kategoria
    }

    fun updateQuery(query: String) {
        _query.value = query
    }
}

fun Ogloszenie.matchesQuery(query: String): Boolean {
    val phrase = query.trim()
    if (phrase.isEmpty()) return true
    return listOf(tytul, tresc, miasto, specjalizacja, tryb)
        .any { it.contains(phrase, ignoreCase = true) }
}
