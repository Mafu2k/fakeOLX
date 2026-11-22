package com.example.fakeolx.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeolx.data.repository.AuthRepository
import com.example.fakeolx.data.repository.OgloszeniaRepository
import com.example.fakeolx.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormUiState(
    val form: OgloszenieForm = OgloszenieForm(),
    val errors: FormErrors = FormErrors(),
    val isLoading: Boolean = false,
    val loadError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val saveError: String? = null
)

class OgloszenieFormViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repository = OgloszeniaRepository()
    private val authRepository = AuthRepository()
    private val editedId: String? = savedStateHandle[Screen.ARG_OGLOSZENIE_ID]

    val isEditMode: Boolean = editedId != null

    private val _state = MutableStateFlow(FormUiState(isLoading = isEditMode))
    val state: StateFlow<FormUiState> = _state.asStateFlow()

    init {
        editedId?.let(::loadOgloszenie)
    }

    fun onFormChange(form: OgloszenieForm) {
        _state.update {
            it.copy(
                form = form,
                errors = if (it.errors.hasErrors) form.validate() else it.errors
            )
        }
    }

    fun save() {
        val form = _state.value.form
        val errors = form.validate()
        if (errors.hasErrors) {
            _state.update { it.copy(errors = errors) }
            return
        }
        val user = authRepository.currentUser
        if (user == null) {
            _state.update { it.copy(saveError = "Sesja wygasła, zaloguj się ponownie") }
            return
        }

        val ogloszenie = form.toOgloszenie(autorId = user.uid, autorEmail = user.email.orEmpty())
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, saveError = null) }
            val result = if (editedId == null) {
                repository.add(ogloszenie)
            } else {
                repository.update(editedId, ogloszenie)
            }
            _state.update {
                result.fold(
                    onSuccess = { _ -> it.copy(isSaving = false, isSaved = true) },
                    onFailure = { error -> it.copy(isSaving = false, saveError = error.toUserMessage()) }
                )
            }
        }
    }

    private fun loadOgloszenie(id: String) {
        viewModelScope.launch {
            val result = repository.getById(id)
            _state.update { current ->
                val ogloszenie = result.getOrNull()
                when {
                    result.isFailure -> current.copy(
                        isLoading = false,
                        loadError = result.exceptionOrNull()?.toUserMessage()
                    )
                    ogloszenie == null -> current.copy(isLoading = false, loadError = "To ogłoszenie zostało usunięte")
                    else -> current.copy(isLoading = false, form = OgloszenieForm.from(ogloszenie))
                }
            }
        }
    }
}
