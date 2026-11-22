package com.example.fakeolx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakeolx.data.model.Ogloszenie
import com.example.fakeolx.data.repository.AuthRepository
import com.example.fakeolx.data.repository.OgloszeniaRepository
import com.example.fakeolx.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class UlubioneViewModel : ViewModel() {
    private val uid = AuthRepository().currentUser?.uid

    val state: StateFlow<ListState> = observeUlubione().toListState(viewModelScope)

    private fun observeUlubione(): Flow<List<Ogloszenie>> {
        val uid = uid ?: return flowOf(emptyList())
        return UserRepository().observeUser(uid)
            .combine(OgloszeniaRepository().observeAll()) { user, ogloszenia ->
                ogloszenia.filter { it.id in user.ulubione }
            }
    }
}
