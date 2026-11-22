package com.example.fakeolx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeolx.data.repository.AuthRepository
import com.example.fakeolx.data.repository.OgloszeniaRepository
import com.example.fakeolx.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfilUiState(
    val isLoading: Boolean = true,
    val displayName: String = "",
    val email: String = "",
    val memberSinceMillis: Long? = null,
    val liczbaOgloszen: Int = 0,
    val liczbaUlubionych: Int = 0
)

class ProfilViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val firebaseUser = authRepository.currentUser
    private val uid = firebaseUser?.uid.orEmpty()
    private val email = firebaseUser?.email.orEmpty()

    private val initialState = ProfilUiState(
        email = email,
        displayName = email.substringBefore("@"),
        memberSinceMillis = firebaseUser?.metadata?.creationTimestamp
    )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    val state: StateFlow<ProfilUiState> = userRepository.observeUser(uid)
        .combine(OgloszeniaRepository().observeByAutor(uid)) { user, ogloszenia ->
            initialState.copy(
                isLoading = false,
                displayName = user.displayName.ifBlank { initialState.displayName },
                liczbaOgloszen = ogloszenia.size,
                liczbaUlubionych = user.ulubione.size
            )
        }
        .catch {
            _message.value = it.toUserMessage()
            emit(initialState.copy(isLoading = false))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialState)

    fun updateDisplayName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            _message.value = "Nazwa nie może być pusta"
            return
        }
        viewModelScope.launch {
            userRepository.updateDisplayName(uid, trimmed)
                .onSuccess { _message.value = "Zapisano nową nazwę" }
                .onFailure { _message.value = it.toUserMessage() }
        }
    }

    fun sendPasswordReset() {
        viewModelScope.launch {
            authRepository.sendPasswordReset(email)
                .onSuccess { _message.value = "Link do zmiany hasła wysłaliśmy na $email" }
                .onFailure { _message.value = it.toUserMessage() }
        }
    }

    fun messageShown() {
        _message.value = null
    }
}
