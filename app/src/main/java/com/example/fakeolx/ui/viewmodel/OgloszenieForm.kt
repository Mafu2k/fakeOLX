package com.example.fakeolx.ui.viewmodel

import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import java.util.Locale

data class OgloszenieForm(
    val tytul: String = "",
    val kategoria: Kategoria = Kategoria.INNE,
    val cena: String = "",
    val miasto: String = "",
    val specjalizacja: String = "",
    val tryb: String = "",
    val opis: String = "",
    val zdjecieUrl: String = ""
) {
    fun withKategoria(nowaKategoria: Kategoria): OgloszenieForm =
        if (nowaKategoria.wymagaSzczegolow) {
            copy(kategoria = nowaKategoria)
        } else {
            copy(kategoria = nowaKategoria, specjalizacja = "", tryb = "")
        }

    fun validate(): FormErrors = FormErrors(
        tytul = when {
            tytul.isBlank() -> "Podaj tytuł ogłoszenia"
            tytul.trim().length > MAX_TYTUL_LENGTH -> "Tytuł może mieć maksymalnie $MAX_TYTUL_LENGTH znaków"
            else -> null
        },
        cena = if (parseCena(cena) == null) "Podaj kwotę, np. 120 albo 49,99" else null,
        opis = if (opis.isBlank()) "Dodaj krótki opis" else null,
        zdjecieUrl = if (zdjecieUrl.isNotBlank() && !zdjecieUrl.trim().startsWith("https://")) {
            "Link musi zaczynać się od https://"
        } else {
            null
        }
    )

    fun toOgloszenie(autorId: String, autorEmail: String): Ogloszenie = Ogloszenie(
        tytul = tytul.trim(),
        tresc = opis.trim(),
        kategoria = kategoria.displayName,
        cena = parseCena(cena) ?: 0.0,
        autorId = autorId,
        autorEmail = autorEmail,
        zdjecieUrl = zdjecieUrl.trim(),
        miasto = miasto.trim(),
        specjalizacja = specjalizacja.trim(),
        tryb = tryb
    )

    companion object {
        const val MAX_TYTUL_LENGTH = 70

        fun from(ogloszenie: Ogloszenie): OgloszenieForm = OgloszenieForm(
            tytul = ogloszenie.tytul,
            kategoria = Kategoria.fromDisplayName(ogloszenie.kategoria),
            cena = formatCenaForInput(ogloszenie.cena),
            miasto = ogloszenie.miasto,
            specjalizacja = ogloszenie.specjalizacja,
            tryb = ogloszenie.tryb,
            opis = ogloszenie.tresc,
            zdjecieUrl = ogloszenie.zdjecieUrl
        )
    }
}

data class FormErrors(
    val tytul: String? = null,
    val cena: String? = null,
    val opis: String? = null,
    val zdjecieUrl: String? = null
) {
    val hasErrors: Boolean
        get() = listOf(tytul, cena, opis, zdjecieUrl).any { it != null }
}

fun parseCena(text: String): Double? =
    text.trim()
        .replace(" ", "")
        .replace(',', '.')
        .toDoubleOrNull()
        ?.takeIf { it >= 0 }

fun formatCenaForInput(cena: Double): String =
    if (cena % 1.0 == 0.0) {
        cena.toLong().toString()
    } else {
        String.format(Locale.forLanguageTag("pl-PL"), "%.2f", cena)
    }
