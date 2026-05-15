package com.example.biofinderandroid

// Imports necesarios del main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.biofinderandroid.Documentos.Animal
import com.example.biofinderandroid.Documentos.BioFinderAjustes
import com.example.biofinderandroid.Documentos.BioFinderDetalle
import com.example.biofinderandroid.Documentos.BioFinderFavoritos
import com.example.biofinderandroid.Documentos.BioFinderFooter
import com.example.biofinderandroid.Documentos.BioFinderHeader
import com.example.biofinderandroid.Documentos.BioFinderHistorial
import com.example.biofinderandroid.Documentos.BioFinderHome
import com.example.biofinderandroid.Documentos.BioFinderLogin
import com.example.biofinderandroid.Documentos.BioFinderModify
import com.example.biofinderandroid.Documentos.BioFinderPerfil
import com.example.biofinderandroid.Documentos.BioFinderRecomendaciones
import com.example.biofinderandroid.Documentos.BioFinderRegister
import com.example.biofinderandroid.Documentos.BioFinderSeleccionImagen
import com.example.biofinderandroid.ui.theme.AppTheme
import com.example.biofinderandroid.ui.theme.backgroundLight

// Clase principal del main
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Me aseguro de que ocupe toda la pantalla mi app
        enableEdgeToEdge()

        setContent {
            // Llamo a mi AppTheme, donde tengo definido mis colores de Material3
            AppTheme {
                // Variables de estado para la navegación
                var pantallaActual by rememberSaveable { mutableStateOf("Login") }
                var animalSeleccionado by remember { mutableStateOf<Animal?>(null) }

                // Variable donde almaceno la especie de animal elegida
                var especieElegida by remember { mutableStateOf("") }

                // Configuro el titulo del header dependiendo de cual sea la pantalla actual
                val tituloHeader = when(pantallaActual) {
                    "Registro" -> "Crea tu cuenta"
                    "Recomendaciones" -> "Recomendaciones"
                    "Home" -> "Home"
                    "Perfil" -> "Perfil"
                    "Ajustes" -> "Ajustes"
                    "Favoritos" -> "Favoritos"
                    "Historial" -> "Historial"
                    "Descripcion" -> "Descripcion"
                    "ModificarPerfil" -> "Modificar"

                    else -> ""
                }

                // Variable que almaceno el usuario que está actualmente logeado
                var userIdLogueado by rememberSaveable { mutableStateOf(-1) }

                // Varias opciones del menu ajustes, que activan o desactivan ciertas opciones
                var mostrarFavoritos by remember { mutableStateOf(true) }
                var mostrarHistorial by remember { mutableStateOf(true) }
                var mostrarRecomendados by remember { mutableStateOf(true) }

                // Scaffold, donde se dibujará todo el contenido en la pantalla
                Scaffold(
                    // Defino mi header
                    topBar = {
                        if (pantallaActual != "Login" && pantallaActual != "Registro") {
                            BioFinderHeader(tituloSeccion = tituloHeader)
                        }
                    },
                    // Defino mi footer, el cual se trata de una barra de navegación
                    bottomBar = {
                        if (pantallaActual != "Login" && pantallaActual != "Registro" && pantallaActual != "Recomendaciones") {
                            BioFinderFooter(
                                pantallaActual = pantallaActual,
                                onNavigate = { pantallaActual = it },
                                mostrarFavoritos = mostrarFavoritos,
                                mostrarHistorial = mostrarHistorial
                            )
                        }
                    },
                    containerColor = backgroundLight // Background de la app
                ) { innerPadding ->


                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                    ) {
                        // Voy cargando cada una de las pantallas segun donde se esté navegando
                        when (pantallaActual) {
                            "Login" -> BioFinderLogin(
                                // Navegacion a registros
                                onNavigateToRegister = { pantallaActual = "Registro" },
                                onNavigateToRecomendations = { id ->
                                    userIdLogueado = id
                                    pantallaActual = "Recomendaciones"
                                }
                            )
                            "Registro" -> BioFinderRegister(
                                // Navegacion al login
                                onNavigateToLogin = { pantallaActual = "Login" }
                            )
                            "Recomendaciones" -> BioFinderRecomendaciones(
                                // Navegacion al home
                                onNavigateToHome = { especie ->
                                    especieElegida = especie
                                    pantallaActual = "Home"
                                }
                            )
                            "Home" -> BioFinderHome(
                                // Navegacion a la descripcion de los animales
                                // La especie seleccionada en preferencias
                                // E indicar si el usuario quiere o no ver los animales recomendados
                                especieSeleccionada = especieElegida,
                                soloRecomendados = mostrarRecomendados,
                                onNavigateToDescripcion = { animal ->
                                    animalSeleccionado = animal
                                    pantallaActual = "Descripcion"
                                }
                            )
                            "Perfil" -> BioFinderPerfil(
                                // Navegación a la página de modificar datos del usuario
                                userIdLogueado = userIdLogueado,
                                onLogout = { pantallaActual = "Login" },
                                onNavigateToEdit = { pantallaActual = "ModificarPerfil" },
                                onNavigateToSelectImage = { pantallaActual = "SeleccionarImagen" } // Navegación al cambio de imagen
                            )
                            "ModificarPerfil" -> BioFinderModify(
                                // Navegacion hacia atras, es decir, a la página del perfil
                                // Y paso el id del usuario actual para saber que datos cargar
                                userIdLogueado = userIdLogueado,
                                onNavigateBack = { pantallaActual = "Perfil" }
                            )
                            "Ajustes" -> BioFinderAjustes(
                                // Paso todas las opciones que se pueden realizar en la pantalla de usuario
                                userId = userIdLogueado,
                                prefFavoritos = mostrarFavoritos,
                                prefHistorial = mostrarHistorial,
                                prefRecomendados = mostrarRecomendados,
                                onPrefsChanged = { fav, hist, rec ->
                                    mostrarFavoritos = fav
                                    mostrarHistorial = hist
                                    mostrarRecomendados = rec
                                },
                                onDeleteAccount = {
                                    userIdLogueado = -1
                                    pantallaActual = "Login"
                                }
                            )
                            "Historial" -> BioFinderHistorial(
                                // Navegación a descripción y paso el id de usuario
                                // para cargar su historial
                                userId = userIdLogueado,
                                onNavigateToDescripcion = { animal ->
                                    animalSeleccionado = animal
                                    pantallaActual = "Descripcion"
                                }
                            )
                            "Favoritos" -> BioFinderFavoritos(
                                // De nuevo navegación a la descripción del animal y
                                // paso el id del usuario para saber cuales son sus favoritos
                                userId = userIdLogueado,
                                onNavigateToDescripcion = { animal ->
                                    animalSeleccionado = animal
                                    pantallaActual = "Descripcion"
                                }
                            )
                            "Descripcion" -> {
                                // Le paso el animal que ha sido selcecionado
                                animalSeleccionado?.let { animal ->
                                    BioFinderDetalle(
                                        animal = animal,
                                        userIdLogueado = userIdLogueado,
                                        onBack = { pantallaActual = "Home" }
                                    )
                                }
                            }
                            "SeleccionarImagen" -> BioFinderSeleccionImagen(
                                userIdLogueado = userIdLogueado,
                                onNavigateBack = { pantallaActual = "Perfil" }
                            )
                        }
                    }
                }
            }
        }
    }
}