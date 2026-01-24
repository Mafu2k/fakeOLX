package com.example.fakeolx.data.model

//Enum kategorii
enum class Kategoria(val displayName: String) {
    ELEKTRONIKA("Elektronika"),
    MOTORYZACJA("Motoryzacja"),
    DOM_OGROD("Dom i Ogród"),
    MODA("Moda"),
    SPORT("Sport i Hobby"),
    KOREPETYCJE("Korepetycje"),
    PRACA("Praca"),
    INNE("Inne");

    companion object {
        fun getAllKategorie(): List<String> = values().map { it.displayName }
    }
}
