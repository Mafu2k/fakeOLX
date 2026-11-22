package com.example.fakeolx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeolx.data.repository.AuthRepository
import com.example.fakeolx.data.repository.OgloszeniaRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

class MojeOgloszeniaViewModel : ViewModel() {
    private val uid = AuthRepository().currentUser?.uid

    val state: StateFlow<ListState> =
        (if (uid == null) flowOf(emptyList()) else OgloszeniaRepository().observeByAutor(uid))
            .toListState(viewModelScope)
}
