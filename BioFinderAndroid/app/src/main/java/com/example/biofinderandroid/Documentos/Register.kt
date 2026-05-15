package com.example.biofinderandroid.Documentos

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biofinderandroid.R
import com.example.biofinderandroid.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun BioFinderRegister(onNavigateToLogin: () -> Unit) {
    // Estados para los inputs
    var username by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    // Para ejecutar la peticion fuera del hilo principal
    val scope = rememberCoroutineScope()

    // Colores de los campos con mis colores de Material3
    val inputColors = TextFieldDefaults.colors(
        focusedContainerColor = surfaceVariantLight,
        unfocusedContainerColor = surfaceVariantLight,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    // Columna principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLight)
            .verticalScroll(rememberScrollState()) // Permito el scroll en la pantalla
            .padding(24.dp)
    ) {
        // La primera fila contiene el conteido de arriba, titulo subtitulo e icono de la derecha
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically, // Lo alineo todo al centro
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Una columna que contendrá los dos textos superiores
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Crea tu cuenta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Únete a nuestra comunidad y descubre el mundo animal",
                    fontSize = 16.sp,
                    color = outlineLight,
                    lineHeight = 20.sp
                )
            }

            // Por último en la columna de arriba el logo de la app
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
        }

        // A continuación la caja surface que contendrá el contenido de los inputs y el boton
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = surfaceContainerHighLight
        ) {
            // Creo una columna dentro del surface
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Todos los input con los elementos de registro utilizo la función que he creado para usar de plantilla
                RegisterField("Nombre de usuario", username, { username = it }, Icons.Default.Person, inputColors)
                DatePickerField(
                    label = "Fecha de nacimiento",
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    icon = Icons.Default.DateRange,
                    colors = inputColors
                )
                RegisterField("Ciudad", city, { city = it }, Icons.Default.LocationOn, inputColors)
                RegisterField("Email", email, { email = it }, Icons.Default.Email, inputColors)
                RegisterField("Contraseña", password, { password = it }, Icons.Default.Lock, inputColors, isPassword = true)

                // Banner con el mensaje de seguridad y los iconos
                Surface(
                    color = Color(0xFFDDE5C9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Contiene una única fila
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Contenido de los iconos y el mensaje de seguridad
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = primaryLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Tu información está segura", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Nunca compartiremos tus datos con terceros", fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.shieldicon),
                            contentDescription = null,
                            tint = primaryLight
                        )
                    }
                }

                // Botón para crear la cuenta
                Button(
                    // Una vez pulsado el botón compruebo que estén todos los campos rellenados
                    onClick = {
                        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || birthDate.isEmpty() || city.isEmpty()){
                            Toast.makeText(context, "Por favor, completa los campos", Toast.LENGTH_SHORT).show()
                        } else {
                            // Realizo las peticiones a la API mediante un hilo secundario, ya que si lo hago en un hilo secundario la app se quedaría congelada
                            scope.launch {
                                try {
                                    // Cojo aquellos datos que el usuario introdujo, con ello creará el json
                                    val request = UserRegisterRequest(
                                        NombreUsuario = username,
                                        Email = email,
                                        FechaNacimiento = birthDate,
                                        Ciudad = city,
                                        Password = password
                                    )

                                    // Envío los datos a la API
                                    val response = RetrofitClient.apiService.registrarUsuario(request)

                                    // Si todo sale correctamente se informa y el usuario será creado
                                    if (response.isSuccessful) {
                                        // EQUIVALE A: alert("¡Registro exitoso!") y redirección en JS
                                        val usuarioCreado = response.body()
                                        Toast.makeText(context, "¡Bienvenido, ${usuarioCreado?.NombreUsuario}!", Toast.LENGTH_LONG).show()
                                        onNavigateToLogin()
                                        // Informo en el caso de que haya un error de datos
                                    } else {
                                        // EQUIVALE A: error de validación (ej: email ya existe)
                                        Toast.makeText(context, "Error: Datos incorrectos o ya registrados", Toast.LENGTH_SHORT).show()
                                    }
                                    // Cualquier otro posible problema con el servidor también lo informo
                                } catch (e: Exception) {
                                    // EQUIVALE A: catch (error) del fetch
                                    Toast.makeText(context, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Crear cuenta", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Componente para la estructura de los inputs
@Composable
fun RegisterField(
    // Creo la sintaxis que tendrá la estructura del componente
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: TextFieldColors,
    isPassword: Boolean = false
) {

    Column {
        // Texto superior que informa de que se trata cada campo
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = onSurfaceLight, modifier = Modifier.padding(bottom = 4.dp))
        // Fila del input
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Esta compuesta por un icono y un texto integrado en el input
            Icon(imageVector = icon, contentDescription = null, tint = onSurfaceVariantLight, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ingresa tu $label", fontSize = 14.sp) },
                shape = RoundedCornerShape(12.dp),
                colors = colors,
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
            )
        }
    }
}

// Componente para el input de la fecha
@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: TextFieldColors
) {
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()

    // Creo el date picker con el formato que espera mi base de datos que es: año,mes y dia
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Formato ISO YYYY-MM-DD para tu API de Python
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onValueChange(formattedDate)
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = onSurfaceLight,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = onSurfaceVariantLight,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            TextField(
                value = value,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                placeholder = { Text("Seleccionar fecha", fontSize = 14.sp) },
                shape = RoundedCornerShape(12.dp),
                colors = colors,
                readOnly = true,
                enabled = false
            )
        }
    }
}