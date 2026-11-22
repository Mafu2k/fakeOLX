package com.example.fakeolx.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidationTest {

    @Test
    fun `rozpoznaje poprawny adres email`() {
        assertTrue(isValidEmail("jan.kowalski@gmail.com"))
        assertTrue(isValidEmail("  jan@uczelnia.edu.pl "))
    }

    @Test
    fun `odrzuca niepoprawny adres email`() {
        assertFalse(isValidEmail("jan.kowalski"))
        assertFalse(isValidEmail("jan@gmail"))
        assertFalse(isValidEmail("jan kowalski@gmail.com"))
    }

    @Test
    fun `logowanie wymaga emaila i hasla`() {
        assertEquals("Podaj e-mail i hasło", validateLogin("", "tajne123"))
        assertNull(validateLogin("jan@gmail.com", "tajne123"))
    }

    @Test
    fun `rejestracja sprawdza dlugosc hasla`() {
        val error = validateRegistration("jan@gmail.com", "12345", "12345")

        assertEquals("Hasło musi mieć co najmniej 6 znaków", error)
    }

    @Test
    fun `rejestracja sprawdza powtorzone haslo`() {
        val error = validateRegistration("jan@gmail.com", "tajne123", "tajne124")

        assertEquals("Hasła nie są takie same", error)
    }

    @Test
    fun `poprawne dane rejestracji przechodza walidacje`() {
        assertNull(validateRegistration("jan@gmail.com", "tajne123", "tajne123"))
    }
}
