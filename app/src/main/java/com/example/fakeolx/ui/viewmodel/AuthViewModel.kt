package com.example.fakeolx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeolx.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isLoggedIn: Boolean get() = repository.currentUser != null

    fun login(email: String, password: String) {
        val error = validateLogin(email, password)
        if (error != null) {
            _authState.value = AuthState.Error(error)
            return
        }
        authenticate { repository.login(email.trim(), password) }
    }

    fun register(email: String, password: String, repeatedPassword: String) {
        val error = validateRegistration(email, password, repeatedPassword)
        if (error != null) {
            _authState.value = AuthState.Error(error)
            return
        }
        authenticate { repository.register(email.trim(), password) }
    }

    fun sendPasswordReset(email: String) {
        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Wpisz adres e-mail, na który wyślemy link")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _authState.value = repository.sendPasswordReset(email.trim()).fold(
                onSuccess = { AuthState.Info("Link do zmiany hasła wysłaliśmy na adres ${email.trim()}") },
                onFailure = { AuthState.Error(it.toUserMessage()) }
            )
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    private fun authenticate(request: suspend () -> Result<*>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _authState.value = request().fold(
                onSuccess = { AuthState.Authenticated },
                onFailure = { AuthState.Error(it.toUserMessage()) }
            )
        }
    }
}

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data object Authenticated : AuthState
    data class Info(val message: String) : AuthState
    data class Error(val message: String) : AuthState
}

const val MIN_PASSWORD_LENGTH = 6

private val emailPattern = Regex("""^[^@\s]+@[^@\s]+\.[^@\s]+$""")

fun isValidEmail(email: String): Boolean = emailPattern.matches(email.trim())

fun validateLogin(email: String, password: String): String? = when {
    email.isBlank() || password.isBlank() -> "Podaj e-mail i hasło"
    !isValidEmail(email) -> "Nieprawidłowy format adresu e-mail"
    else -> null
}

fun validateRegistration(email: String, password: String, repeatedPassword: String): String? = when {
    email.isBlank() || password.isBlank() -> "Podaj e-mail i hasło"
    !isValidEmail(email) -> "Nieprawidłowy format adresu e-mail"
    password.length < MIN_PASSWORD_LENGTH -> "Hasło musi mieć co najmniej $MIN_PASSWORD_LENGTH znaków"
    password != repeatedPassword -> "Hasła nie są takie same"
    else -> null
}
