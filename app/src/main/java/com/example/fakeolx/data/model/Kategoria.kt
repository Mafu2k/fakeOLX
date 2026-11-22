package com.example.fakeolx.data.model

enum class Kategoria(val displayName: String) {
    ELEKTRONIKA("Elektronika"),
    MOTORYZACJA("Motoryzacja"),
    DOM_OGROD("Dom i Ogród"),
    MODA("Moda"),
    SPORT("Sport i Hobby"),
    KOREPETYCJE("Korepetycje"),
    PRACA("Praca"),
    INNE("Inne");

    val wymagaSzczegolow: Boolean
        get() = this == PRACA || this == KOREPETYCJE

    companion object {
        fun fromDisplayName(name: String): Kategoria =
            entries.firstOrNull { it.displayName == name } ?: INNE
    }
}
