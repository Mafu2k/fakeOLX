package com.example.fakeolx.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class Ogloszenie(
    @get:Exclude val id: String = "",
    val tytul: String = "",
    val tresc: String = "",
    val kategoria: String = "",
    val cena: Double = 0.0,
    val autorId: String = "",
    val autorEmail: String = "",
    val zdjecieUrl: String = "",
    val dataUtworzenia: Timestamp = Timestamp.now(),
    val miasto: String = "",
    val specjalizacja: String = "",
    val tryb: String = ""
)
