package com.example.fakeolx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeolx.data.model.Ogloszenie
import com.example.fakeolx.data.repository.AuthRepository
import com.example.fakeolx.data.repository.OgloszeniaRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

//ViewModel ogloszen
class OgloszeniaViewModel : ViewModel() {
    private val repository = OgloszeniaRepository()
    private val authRepository = AuthRepository()
    private var ogloszeniaJob: Job? = null

    private val _ogloszenia = MutableStateFlow<List<Ogloszenie>>(emptyList())
    val ogloszenia: StateFlow<List<Ogloszenie>> = _ogloszenia

    private val _selectedOgloszenie = MutableStateFlow<Ogloszenie?>(null)
    val selectedOgloszenie: StateFlow<Ogloszenie?> = _selectedOgloszenie

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _selectedKategoria = MutableStateFlow<String?>(null)
    val selectedKategoria: StateFlow<String?> = _selectedKategoria

    init {
        loadAllOgloszenia()
    }

    private fun mapErrorMessage(error: Throwable): String {
        if (error is FirebaseFirestoreException &&
            error.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION
        ) {
            return "Nie udało się pobrać listy ogłoszeń."
        }
        return error.message ?: "Błąd pobierania"
    }

    private fun observeOgloszenia(source: kotlinx.coroutines.flow.Flow<List<Ogloszenie>>) {
        ogloszeniaJob?.cancel()
        ogloszeniaJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            source
                .catch { error ->
                    _ogloszenia.value = emptyList()
                    _uiState.value = UiState.Error(mapErrorMessage(error))
                }
                .collect { lista ->
                    _ogloszenia.value = lista
                    _uiState.value = UiState.Success
                }
        }
    }

    //Laduj wszystkie ogloszenia
    fun loadAllOgloszenia() {
        observeOgloszenia(repository.getAllOgloszenia())
    }

    //Filtruj po kategorii
    fun filterByKategoria(kategoria: String?) {
        _selectedKategoria.value = kategoria
        if (kategoria == null) {
            loadAllOgloszenia()
        } else {
            observeOgloszenia(repository.getOgloszeniaBykategoria(kategoria))
        }
    }

    //Laduj moje ogloszenia
    fun loadMojeOgloszenia() {
        val userId = authRepository.currentUser?.uid ?: return
        observeOgloszenia(repository.getMojeOgloszenia(userId))
    }

    //Wybierz ogloszenie
    fun selectOgloszenie(ogloszenieId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getOgloszenieById(ogloszenieId)
            if (result.isSuccess) {
                _selectedOgloszenie.value = result.getOrNull()
                _uiState.value = UiState.Success
            } else {
                _uiState.value = UiState.Error(result.exceptionOrNull()?.message ?: "Błąd")
            }
        }
    }

    //Dodaj ogloszenie
    fun addOgloszenie(
        tytul: String,
        tresc: String,
        kategoria: String,
        cena: Double,
        miasto: String,
        specjalizacja: String = "",
        tryb: String = "",
        zdjecieUrl: String = ""
    ) {
        val user = authRepository.currentUser
        if (user == null) {
            _uiState.value = UiState.Error("Musisz być zalogowany")
            return
        }

        if (tytul.isBlank() || tresc.isBlank() || kategoria.isBlank()) {
            _uiState.value = UiState.Error("Wypełnij wszystkie pola")
            return
        }

        val ogloszenie = Ogloszenie(
            tytul = tytul,
            tresc = tresc,
            kategoria = kategoria,
            cena = cena,
            miasto = miasto,
            specjalizacja = specjalizacja,
            tryb = tryb,
            autorId = user.uid,
            autorEmail = user.email ?: "",
            zdjecieUrl = zdjecieUrl
        )

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.addOgloszenie(ogloszenie)
            _uiState.value = if (result.isSuccess) {
                UiState.Success
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Błąd dodawania")
            }
        }
    }

    //Aktualizuj ogloszenie
    fun updateOgloszenie(
        id: String,
        tytul: String,
        tresc: String,
        kategoria: String,
        cena: Double,
        miasto: String,
        specjalizacja: String,
        tryb: String,
        zdjecieUrl: String
    ) {
        val user = authRepository.currentUser ?: return

        val ogloszenie = Ogloszenie(
            id = id,
            tytul = tytul,
            tresc = tresc,
            kategoria = kategoria,
            cena = cena,
            miasto = miasto,
            specjalizacja = specjalizacja,
            tryb = tryb,
            autorId = user.uid,
            autorEmail = user.email ?: "",
            zdjecieUrl = zdjecieUrl
        )

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.updateOgloszenie(id, ogloszenie)
            _uiState.value = if (result.isSuccess) {
                UiState.Success
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Błąd aktualizacji")
            }
        }
    }

    //Usun ogloszenie
    fun deleteOgloszenie(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.deleteOgloszenie(id)
            _uiState.value = if (result.isSuccess) {
                UiState.Success
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Błąd usuwania")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
}

//Stany UI
sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Success : UiState()
    data class Error(val message: String) : UiState()
}
