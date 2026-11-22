package com.example.fakeolx.ui.components

import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val polishLocale: Locale = Locale.forLanguageTag("pl-PL")

fun formatPrice(amount: Double): String {
    if (amount == 0.0) return "Za darmo"
    val formatted = if (amount % 1.0 == 0.0) {
        amount.toLong().toString()
    } else {
        String.format(polishLocale, "%.2f", amount)
    }
    return "$formatted zł"
}

fun Ogloszenie.priceLabel(): String {
    val price = formatPrice(cena)
    val isHourlyRate = Kategoria.fromDisplayName(kategoria) == Kategoria.KOREPETYCJE && cena > 0
    return if (isHourlyRate) "$price/h" else price
}

fun formatDate(timestamp: Timestamp): String =
    SimpleDateFormat("d MMMM yyyy, HH:mm", polishLocale).format(timestamp.toDate())

fun formatMonthYear(millis: Long): String =
    SimpleDateFormat("MMMM yyyy", polishLocale).format(Date(millis))
