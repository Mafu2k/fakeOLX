package com.example.fakeolx.ui.viewmodel

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException

fun Throwable.toUserMessage(): String = when (this) {
    is FirebaseAuthWeakPasswordException -> "Hasło jest za słabe, użyj co najmniej 6 znaków"
    is FirebaseAuthUserCollisionException -> "Konto z tym adresem e-mail już istnieje"
    is FirebaseAuthInvalidUserException -> "Nie znaleziono konta z tym adresem e-mail"
    is FirebaseAuthInvalidCredentialsException -> "Nieprawidłowy e-mail lub hasło"
    is FirebaseNetworkException -> "Brak połączenia z internetem"
    is FirebaseFirestoreException -> when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> "Brak uprawnień do wykonania tej operacji"
        FirebaseFirestoreException.Code.UNAVAILABLE -> "Serwer jest chwilowo niedostępny, spróbuj ponownie"
        else -> "Nie udało się połączyć z bazą danych"
    }
    else -> message ?: "Wystąpił nieoczekiwany błąd"
}
