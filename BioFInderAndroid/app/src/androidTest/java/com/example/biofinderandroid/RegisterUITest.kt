package com.example.biofinderandroid

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import com.example.biofinderandroid.Documentos.BioFinderRegister
import org.junit.Test

class BioFinderRegisterUITest {

    // Prepara el entorno virtual
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verificarNodosYComportamientoInputUsuario() {
        // Cargo la pantalla y desactiva la navegación, ya que en este test lo que me interesa
        // es la pantalla de registro
        composeTestRule.setContent {
            BioFinderRegister(onNavigateToLogin = {})
        }

        // Verificación de nodos, comprueba que existe el texto
        composeTestRule.onNodeWithText("Crea tu cuenta").assertIsDisplayed()

        // Compruebo el funcionamiento de los componentes
        // Verifico si existe el input con label "Ingresa tu nombre de usuario"
        composeTestRule.onNodeWithText("Ingresa tu Nombre de usuario")
            .assertIsDisplayed()
            .performTextReplacement("Salva")
    }
}

