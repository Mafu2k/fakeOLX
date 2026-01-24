package com.example.fakeolx.data.model

import com.google.firebase.Timestamp

//Model ogloszenia
data class Ogloszenie(
    val id: String = "",
    val tytul: String = "",
    val tresc: String = "",
    val kategoria: String = "",
    val cena: Double = 0.0,
    val autorId: String = "",
    val autorEmail: String = "",
    val zdjecieUrl: String = "",
    val dataUtworzenia: Timestamp = Timestamp.now(),
    val miasto: String = ""
)
