package com.example.biofinderandroid.Documentos

// Imports necesarios
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

// Funcion principal para la pagina de modificar informacion del usuario
@Composable
fun BioFinderModify(
    // Variables de entrada
    userIdLogueado: Int,
    onNavigateBack: () -> Unit // Para navegar de nuevo al perfil una vez se haya cambiado la información
) {
    // Variables que se utilizaran para la modificación de los datos
    var username by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Context para mostrar los toast y scope para la llamada a la API
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Cargo la información actual del usuario defecto en cada input
    LaunchedEffect(Unit) {
        try {
            // Llamo a la API para obtener la información del usuario
            val response = RetrofitClient.apiService.obtenerPerfil(userIdLogueado)
            // Si la respuesta es correcta cargo cada dato en su respectivo input
            if (response.isSuccessful) {
                val d = response.body()
                username = d?.NombreUsuario ?: ""
                birthDate = d?.FechaNacimiento ?: ""
                city = d?.Ciudad ?: ""
                email = d?.Email ?: ""
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar datos actuales", Toast.LENGTH_SHORT).show()
        }
    }

    // Diseño de la pantalla de modificar
    Column(
        modifier = Modifier.fillMaxSize().background(backgroundLight)
            .verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        // Texto superior con el logo de la aplicación
        Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Modificar perfil", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = onSurfaceLight)
                Text("Actualiza tu información personal", fontSize = 16.sp, color = outlineLight)
            }
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.size(80.dp))
        }

        // Card con la información para cambiar los datos
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp), color = surfaceContainerHighLight) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                RegisterFieldModify("Nombre de usuario", username, { username = it }, Icons.Default.Person, TextFieldDefaults.colors())
                DatePickerFieldModify("Fecha de nacimiento", birthDate, { birthDate = it }, Icons.Default.DateRange, TextFieldDefaults.colors())
                RegisterFieldModify("Ciudad", city, { city = it }, Icons.Default.LocationOn, TextFieldDefaults.colors())
                RegisterFieldModify("Email", email, { email = it }, Icons.Default.Email, TextFieldDefaults.colors())
                RegisterFieldModify("Nueva Contraseña", password, { password = it }, Icons.Default.Lock, TextFieldDefaults.colors(), isPassword = true)

                //Botón para guardar los cambios aplicados
                Button(
                    onClick = {
                        if (password.isEmpty()) {
                            // Si no se ha ingresado una contraseña informo
                            Toast.makeText(context, "Por favor, confirma tu contraseña", Toast.LENGTH_SHORT).show()
                        } else {
                            // Si todos los datos están rellenados llamo al endpoint de la API que actualiza los datos del usuario
                            scope.launch {
                                try {
                                    val request = UserRegisterRequest(username, email, birthDate, city, password)
                                    val response = RetrofitClient.apiService.actualizarUsuario(userIdLogueado, request)
                                    // Si todo es correcto informo con otro mensaje Toast
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "¡Información actualizada!", Toast.LENGTH_SHORT).show()
                                        onNavigateBack() // Volvemos al perfil
                                    } else {
                                        Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    // Texto del botón para guardar cambios
                    Text("Guardar cambios", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Componente para la estructura de los inputs
@Composable
fun RegisterFieldModify(
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
fun DatePickerFieldModify(
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
            // Formato  YYYY-MM-DD para ser compatible con mi base de datos
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onValueChange(formattedDate)
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )

    // Estructura del datapicker
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