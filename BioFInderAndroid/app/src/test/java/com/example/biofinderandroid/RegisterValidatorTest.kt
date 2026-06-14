package com.example.biofinderandroid

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterValidatorTest {

    @Test
    fun formularioIncompleto_deberiaDevolverFalse() {
        // Simulo que el usuario no ha rellenado todos los campos para comprobar que efectivamrnte devuelve False
        val resultado = RegisterValidator.esFormularioValido(
            username = "Salva",
            email = "salva@gmail.com",
            password = "password123",
            birthDate = "",
            city = ""
        )
        // Debe devolver False
        assertFalse(resultado)
    }

    @Test
    fun formularioCompleto_deberiaDevolverTrue() {
        // Simulo que el usuario ha rellenado todos los campos para comprobar que devuelve True
        val resultado = RegisterValidator.esFormularioValido(
            username = "Steven",
            email = "steven@gmail.com",
            password = "stevenpassword",
            birthDate = "2023-12-23",
            city = "Sagunto"
        )
        // Debe devolver True
        assertTrue(resultado)
    }
}

object RegisterValidator {
    fun esFormularioValido(username: String, email: String, password: String, birthDate: String, city: String): Boolean {
        return !(username.isEmpty() || email.isEmpty() || password.isEmpty() || birthDate.isEmpty() || city.isEmpty())
    }
}