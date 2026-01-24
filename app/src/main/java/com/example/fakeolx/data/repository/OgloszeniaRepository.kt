package com.example.fakeolx.data.repository

import com.example.fakeolx.data.model.Ogloszenie
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

//Repozytorium ogloszen
class OgloszeniaRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("ogloszenia")

    //Pobierz wszystkie ogloszenia
    fun getAllOgloszenia(): Flow<List<Ogloszenie>> = callbackFlow {
        val listener = collection
            .orderBy("dataUtworzenia", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val ogloszenia = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Ogloszenie::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(ogloszenia)
            }
        awaitClose { listener.remove() }
    }

    //Pobierz ogloszenia po kategorii
    fun getOgloszeniaBykategoria(kategoria: String): Flow<List<Ogloszenie>> = callbackFlow {
        val listener = collection
            .whereEqualTo("kategoria", kategoria)
            .orderBy("dataUtworzenia", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val ogloszenia = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Ogloszenie::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(ogloszenia)
            }
        awaitClose { listener.remove() }
    }

    //Pobierz moje ogloszenia
    fun getMojeOgloszenia(userId: String): Flow<List<Ogloszenie>> = callbackFlow {
        val listener = collection
            .whereEqualTo("autorId", userId)
            .orderBy("dataUtworzenia", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val ogloszenia = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Ogloszenie::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(ogloszenia)
            }
        awaitClose { listener.remove() }
    }

    //Pobierz ogloszenie po id
    suspend fun getOgloszenieById(id: String): Result<Ogloszenie> {
        return try {
            val doc = collection.document(id).get().await()
            val ogloszenie = doc.toObject(Ogloszenie::class.java)?.copy(id = doc.id)
            if (ogloszenie != null) {
                Result.success(ogloszenie)
            } else {
                Result.failure(Exception("Ogłoszenie nie znalezione"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Dodaj ogloszenie
    suspend fun addOgloszenie(ogloszenie: Ogloszenie): Result<String> {
        return try {
            val docRef = collection.add(ogloszenie).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Aktualizuj ogloszenie
    suspend fun updateOgloszenie(id: String, ogloszenie: Ogloszenie): Result<Unit> {
        return try {
            collection.document(id).set(ogloszenie).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Usun ogloszenie
    suspend fun deleteOgloszenie(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
