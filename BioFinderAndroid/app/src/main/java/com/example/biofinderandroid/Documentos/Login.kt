package com.example.biofinderandroid.Documentos

// Imports necesarios para el funcionamiento del login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biofinderandroid.R
import com.example.biofinderandroid.ui.theme.*
import kotlinx.coroutines.launch


// Función del login
@Composable
fun BioFinderLogin(
    onNavigateToRegister: () -> Unit,
    onNavigateToRecomendations: (Int) -> Unit // <--- Añade (Int) aquí
) {

    // Variables de los inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Variable para mostrar el Toast (Mensaje informativo si no se rellenan los inputs)
    val context = LocalContext.current

    // Variables de colores de Material3 guardadas en colors
    val inputColors = TextFieldDefaults.colors(
        focusedContainerColor = surfaceVariantLight,
        unfocusedContainerColor = surfaceVariantLight,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    // Para ejecutar la comunicación con la API en segundo plano
    val scope = rememberCoroutineScope()

    // Columna principal donde estarán todos los elementos
    Column(
        modifier = Modifier
            // Le digo que ocupe toda la pantalla, asigno color y que la caja
            // no llegue hasta los bordes
            .fillMaxSize()
            .background(backgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        // Alineo al centro
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo de la aplicación
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "BioFinder Logo",
            modifier = Modifier
                .size(350.dp)
                .offset(y = 20.dp)
        )

        // Utilizo surface para la caja donde están los demás elementos
        // permite poner colores de fondo, sombras, etc. Es más inteligente
        // que un simple box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            color = surfaceContainerHighLight
        ) {
            // Todo irá dentro de una columna
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titulo y subtitulo
                Text(
                    text = "Bienvenido",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceLight
                )
                Text(
                    text = "Ingresa tus credenciales",
                    fontSize = 16.sp,
                    color = outlineLight,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Input del email
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Ingresa tu email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = inputColors,
                    singleLine = true
                )

                // Espaciado entre un input y otro
                Spacer(modifier = Modifier.height(12.dp))

                // Input de la contraseña
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Ingresa tu contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = inputColors,
                    // Le pongo los puntitos para que no se vea la contraseña
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                // Caja con el texto de olvidaste contraseña
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontSize = 13.sp,
                        color = primaryLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterStart)
                            // Hago el texto clickable para direccionar a la pagina de registro
                            .clickable { /* Acción */ }

                    )
                }
                // Un pequeño espaciado con el boton
                Spacer(modifier = Modifier.height(24.dp))

                // Boton para iniciar sesión
                Button(
                    // Compruebo que se rellenen los campos
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                        } else {
                            // Realizo la comunicación con la API en un hilo diferente
                            scope.launch {
                                try {
                                    // Verifico en response que las credenciales sean correctas
                                    val credenciales = UserLoginRequest(Email = email, Password = password)
                                    val response = RetrofitClient.apiService.loginUsuario(credenciales)
                                    // Si son correctas, dejo entrar a la app al usuario correspondiente
                                    if (response.isSuccessful) {
                                        val body = response.body()
                                        val idUsuario = body?.id ?: -1 // Suponiendo que tu respuesta tiene el campo 'id'
                                        onNavigateToRecomendations(idUsuario) // <--- Pasamos el ID aquí
                                    } else {
                                        // Informo si los datos no son correctos
                                        Toast.makeText(context, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                    }
                                    // Cualquier otro error informo también
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Iniciar sesión", color = Color.White, fontSize = 18.sp)
                }
            }
        }

        // Otro espaciado con el texto de abajo
        Spacer(modifier = Modifier.height(40.dp))

        // Texto inferior
        Text(
            text = buildAnnotatedString {
                append("¿No tienes cuenta? ")
                // Aplico solo el color de texto azul al Registrate aquí con withStyle y separando los textos con append
                withStyle(style = SpanStyle(
                    color = Color(0xFF3B71CA),
                    fontWeight = FontWeight.Bold)
                ) {
                    append("Regístrate aquí")
                }
            },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .clickable { onNavigateToRegister() },
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}