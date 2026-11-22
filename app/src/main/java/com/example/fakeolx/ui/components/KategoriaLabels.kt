package com.example.fakeolx.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fakeolx.data.model.Kategoria

val Kategoria.icon: ImageVector
    get() = when (this) {
        Kategoria.ELEKTRONIKA -> Icons.Default.PhoneAndroid
        Kategoria.MOTORYZACJA -> Icons.Default.DirectionsCar
        Kategoria.DOM_OGROD -> Icons.Default.Chair
        Kategoria.MODA -> Icons.Default.Checkroom
        Kategoria.SPORT -> Icons.Default.SportsSoccer
        Kategoria.KOREPETYCJE -> Icons.Default.School
        Kategoria.PRACA -> Icons.Default.Work
        Kategoria.INNE -> Icons.Default.MoreHoriz
    }

val Kategoria.specjalizacjaLabel: String
    get() = if (this == Kategoria.PRACA) "Stanowisko" else "Przedmiot"

val Kategoria.trybLabel: String
    get() = if (this == Kategoria.PRACA) "Tryb pracy" else "Tryb zajęć"

val Kategoria.trybOptions: List<String>
    get() = if (this == Kategoria.PRACA) {
        listOf("Zdalnie", "Hybryda", "Stacjonarnie")
    } else {
        listOf("Online", "Stacjonarnie", "Hybryda")
    }

val Kategoria.cenaLabel: String
    get() = when (this) {
        Kategoria.KOREPETYCJE -> "Stawka za godzinę (zł) *"
        Kategoria.PRACA -> "Wynagrodzenie miesięczne (zł) *"
        else -> "Cena (zł) *"
    }
