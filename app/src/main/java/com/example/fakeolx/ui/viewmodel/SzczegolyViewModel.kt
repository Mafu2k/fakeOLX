package com.example.fakeolx.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeolx.data.model.Ogloszenie
import com.example.fakeolx.data.repository.AuthRepository
import com.example.fakeolx.data.repository.OgloszeniaRepository
import com.example.fakeolx.data.repository.UserRepository
import com.example.fakeolx.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SzczegolyState {
    data object Loading : SzczegolyState
    data class Loaded(val ogloszenie: Ogloszenie, val isAuthor: Boolean) : SzczegolyState
    data object NotFound : SzczegolyState
    data class Error(val message: String) : SzczegolyState
}

class SzczegolyViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val ogloszenieId: String = checkNotNull(savedStateHandle[Screen.ARG_OGLOSZENIE_ID])
    private val repository = OgloszeniaRepository()
    private val userRepository = UserRepository()
    private val uid = AuthRepository().currentUser?.uid

    val state: StateFlow<SzczegolyState> = repository.observeById(ogloszenieId)
        .map { ogloszenie ->
            if (ogloszenie == null) {
                SzczegolyState.NotFound
            } else {
                SzczegolyState.Loaded(ogloszenie, isAuthor = ogloszenie.autorId == uid)
            }
        }
        .catch { emit(SzczegolyState.Error(it.toUserMessage())) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SzczegolyState.Loading)

    val isUlubione: StateFlow<Boolean> =
        (if (uid == null) flowOf(false) else userRepository.observeUser(uid).map { ogloszenieId in it.ulubione })
            .catch { emit(false) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun toggleUlubione() {
        val uid = uid ?: return
        val dodaj = !isUlubione.value
        viewModelScope.launch {
            userRepository.setUlubione(uid, ogloszenieId, dodaj)
                .onSuccess {
                    _message.value = if (dodaj) "Dodano do ulubionych" else "Usunięto z ulubionych"
                }
                .onFailure { _message.value = it.toUserMessage() }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(ogloszenieId)
                .onSuccess { _isDeleted.value = true }
                .onFailure { _message.value = it.toUserMessage() }
        }
    }

    fun showMessage(text: String) {
        _message.value = text
    }

    fun messageShown() {
        _message.value = null
    }
}
