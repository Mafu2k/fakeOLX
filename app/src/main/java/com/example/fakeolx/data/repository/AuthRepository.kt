package com.example.fakeolx.data.repository

import com.example.fakeolx.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//Repozytorium autoryzacji
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    //Rejestracja
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            
            //Zapisz dane uzytkownika w Firestore
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                displayName = email.substringBefore("@")
            )
            firestore.collection("users").document(firebaseUser.uid).set(user).await()
            
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Logowanie
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Wylogowanie
    fun logout() {
        auth.signOut()
    }
}
