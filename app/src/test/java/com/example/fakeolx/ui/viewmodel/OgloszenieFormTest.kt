package com.example.fakeolx.ui.viewmodel

import com.example.fakeolx.data.model.Kategoria
import com.example.fakeolx.data.model.Ogloszenie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OgloszenieFormTest {

    private val poprawnyFormularz = OgloszenieForm(
        tytul = "Rower górski",
        kategoria = Kategoria.SPORT,
        cena = "850",
        miasto = "Gliwice",
        opis = "Rama M, nowe opony"
    )

    @Test
    fun `poprawny formularz nie ma bledow`() {
        assertFalse(poprawnyFormularz.validate().hasErrors)
    }

    @Test
    fun `pusty formularz zglasza tytul cene i opis`() {
        val errors = OgloszenieForm().validate()

        assertNotNull(errors.tytul)
        assertNotNull(errors.cena)
        assertNotNull(errors.opis)
        assertNull(errors.zdjecieUrl)
    }

    @Test
    fun `za dlugi tytul jest bledem`() {
        val form = poprawnyFormularz.copy(tytul = "a".repeat(OgloszenieForm.MAX_TYTUL_LENGTH + 1))

        assertNotNull(form.validate().tytul)
    }

    @Test
    fun `link do zdjecia musi byc https`() {
        val form = poprawnyFormularz.copy(zdjecieUrl = "http://example.com/rower.jpg")

        assertNotNull(form.validate().zdjecieUrl)
    }

    @Test
    fun `cena z przecinkiem jest akceptowana`() {
        assertEquals(49.99, parseCena("49,99")!!, 0.001)
        assertEquals(1200.0, parseCena("1 200")!!, 0.001)
        assertEquals(0.0, parseCena("0")!!, 0.001)
    }

    @Test
    fun `ujemna albo tekstowa cena jest odrzucana`() {
        assertNull(parseCena("-10"))
        assertNull(parseCena("dużo"))
        assertNull(parseCena(""))
    }

    @Test
    fun `zmiana kategorii na zwykla czysci pola pracy i korepetycji`() {
        val korepetycje = poprawnyFormularz.copy(
            kategoria = Kategoria.KOREPETYCJE,
            specjalizacja = "Matematyka",
            tryb = "Online"
        )

        val poZmianie = korepetycje.withKategoria(Kategoria.ELEKTRONIKA)

        assertEquals("", poZmianie.specjalizacja)
        assertEquals("", poZmianie.tryb)
    }

    @Test
    fun `zmiana miedzy praca a korepetycjami zostawia pola`() {
        val praca = poprawnyFormularz.copy(kategoria = Kategoria.PRACA, specjalizacja = "Kelner")

        assertEquals("Kelner", praca.withKategoria(Kategoria.KOREPETYCJE).specjalizacja)
    }

    @Test
    fun `toOgloszenie przycina tekst i zapisuje nazwe kategorii`() {
        val form = poprawnyFormularz.copy(tytul = "  Rower górski  ", cena = "49,50")

        val ogloszenie = form.toOgloszenie(autorId = "uid-1", autorEmail = "jan@example.com")

        assertEquals("Rower górski", ogloszenie.tytul)
        assertEquals("Sport i Hobby", ogloszenie.kategoria)
        assertEquals(49.5, ogloszenie.cena, 0.001)
        assertEquals("uid-1", ogloszenie.autorId)
    }

    @Test
    fun `formularz edycji odtwarza dane ogloszenia`() {
        val ogloszenie = Ogloszenie(
            tytul = "Korepetycje z fizyki",
            tresc = "Liceum, matura",
            kategoria = "Korepetycje",
            cena = 60.5,
            specjalizacja = "Fizyka",
            tryb = "Online"
        )

        val form = OgloszenieForm.from(ogloszenie)

        assertEquals(Kategoria.KOREPETYCJE, form.kategoria)
        assertEquals("60,50", form.cena)
        assertEquals("Liceum, matura", form.opis)
        assertTrue(form.kategoria.wymagaSzczegolow)
    }
}
