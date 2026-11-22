package com.example.fakeolx.data.repository

import com.example.fakeolx.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val userRepository: UserRepository = UserRepository()
) {
    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun register(email: String, password: String): Result<FirebaseUser> = runCatching {
        val firebaseUser = checkNotNull(auth.createUserWithEmailAndPassword(email, password).await().user)
        userRepository.createUser(
            User(
                uid = firebaseUser.uid,
                email = email,
                displayName = email.substringBefore("@")
            )
        )
        firebaseUser
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> = runCatching {
        checkNotNull(auth.signInWithEmailAndPassword(email, password).await().user)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() {
        auth.signOut()
    }
}
