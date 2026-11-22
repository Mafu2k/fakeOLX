package com.example.fakeolx.ui.components

import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import com.example.fakeolx.ui.viewmodel.matchesQuery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormattingTest {

    @Test
    fun `cena calkowita bez groszy`() {
        assertEquals("120 zł", formatPrice(120.0))
    }

    @Test
    fun `cena z groszami po polsku`() {
        assertEquals("49,99 zł", formatPrice(49.99))
    }

    @Test
    fun `zero oznacza za darmo`() {
        assertEquals("Za darmo", formatPrice(0.0))
    }

    @Test
    fun `stawka korepetycji jest za godzine`() {
        val ogloszenie = Ogloszenie(kategoria = Kategoria.KOREPETYCJE.displayName, cena = 60.0)

        assertEquals("60 zł/h", ogloszenie.priceLabel())
    }

    @Test
    fun `nieznana kategoria trafia do inne`() {
        assertEquals(Kategoria.INNE, Kategoria.fromDisplayName("Antyki"))
        assertEquals(Kategoria.DOM_OGROD, Kategoria.fromDisplayName("Dom i Ogród"))
    }

    @Test
    fun `wyszukiwanie ignoruje wielkosc liter i obejmuje miasto`() {
        val ogloszenie = Ogloszenie(tytul = "Rower Kross", tresc = "Stan dobry", miasto = "Gliwice")

        assertTrue(ogloszenie.matchesQuery("rower"))
        assertTrue(ogloszenie.matchesQuery(" gliwice "))
        assertTrue(ogloszenie.matchesQuery(""))
        assertFalse(ogloszenie.matchesQuery("laptop"))
    }
}
