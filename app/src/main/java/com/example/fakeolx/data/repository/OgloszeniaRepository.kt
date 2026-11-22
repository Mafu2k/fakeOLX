package com.example.fakeolx.data.repository

import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class OgloszeniaRepository {
    private val collection = FirebaseFirestore.getInstance().collection("ogloszenia")

    fun observeAll(): Flow<List<Ogloszenie>> =
        collection.orderBy("dataUtworzenia", Query.Direction.DESCENDING).observeOgloszenia()

    // whereEqualTo + orderBy wymaga indeksu złożonego w Firestore, więc te listy sortuję po stronie aplikacji
    fun observeByKategoria(kategoria: Kategoria): Flow<List<Ogloszenie>> =
        collection.whereEqualTo("kategoria", kategoria.displayName)
            .observeOgloszenia()
            .map(::sortByNewest)

    fun observeByAutor(autorId: String): Flow<List<Ogloszenie>> =
        collection.whereEqualTo("autorId", autorId)
            .observeOgloszenia()
            .map(::sortByNewest)

    fun observeById(id: String): Flow<Ogloszenie?> = callbackFlow {
        val registration = collection.document(id).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toOgloszenie())
        }
        awaitClose { registration.remove() }
    }

    suspend fun getById(id: String): Result<Ogloszenie?> = runCatching {
        collection.document(id).get().await().toOgloszenie()
    }

    suspend fun add(ogloszenie: Ogloszenie): Result<Unit> = runCatching {
        collection.add(ogloszenie).await()
    }

    suspend fun update(id: String, ogloszenie: Ogloszenie): Result<Unit> = runCatching {
        val editableFields = mapOf(
            "tytul" to ogloszenie.tytul,
            "tresc" to ogloszenie.tresc,
            "kategoria" to ogloszenie.kategoria,
            "cena" to ogloszenie.cena,
            "miasto" to ogloszenie.miasto,
            "specjalizacja" to ogloszenie.specjalizacja,
            "tryb" to ogloszenie.tryb,
            "zdjecieUrl" to ogloszenie.zdjecieUrl
        )
        collection.document(id).update(editableFields).await()
    }

    suspend fun delete(id: String): Result<Unit> = runCatching {
        collection.document(id).delete().await()
    }

    private fun Query.observeOgloszenia(): Flow<List<Ogloszenie>> = callbackFlow {
        val registration = addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.documents.orEmpty().mapNotNull { it.toOgloszenie() })
        }
        awaitClose { registration.remove() }
    }

    private fun DocumentSnapshot.toOgloszenie(): Ogloszenie? =
        toObject(Ogloszenie::class.java)?.copy(id = id)

    private fun sortByNewest(ogloszenia: List<Ogloszenie>): List<Ogloszenie> =
        ogloszenia.sortedByDescending { it.dataUtworzenia }
}
