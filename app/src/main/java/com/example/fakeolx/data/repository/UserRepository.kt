package com.example.fakeolx.data.repository

import com.example.fakeolx.data.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val users = FirebaseFirestore.getInstance().collection("users")

    fun observeUser(uid: String): Flow<User> = callbackFlow {
        val registration = users.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(User::class.java) ?: User(uid = uid))
        }
        awaitClose { registration.remove() }
    }

    suspend fun createUser(user: User) {
        users.document(user.uid).set(user).await()
    }

    suspend fun updateDisplayName(uid: String, displayName: String): Result<Unit> = runCatching {
        users.document(uid).set(mapOf("displayName" to displayName), SetOptions.merge()).await()
    }

    suspend fun setUlubione(uid: String, ogloszenieId: String, ulubione: Boolean): Result<Unit> = runCatching {
        val change = if (ulubione) {
            FieldValue.arrayUnion(ogloszenieId)
        } else {
            FieldValue.arrayRemove(ogloszenieId)
        }
        users.document(uid).set(mapOf("ulubione" to change), SetOptions.merge()).await()
    }
}
