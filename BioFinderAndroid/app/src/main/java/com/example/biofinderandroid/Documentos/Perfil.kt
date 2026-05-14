package com.example.biofinderandroid.Documentos

// Imports del perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.biofinderandroid.R
import com.example.biofinderandroid.ui.theme.*

// Función principal del perfil
@Composable
fun BioFinderPerfil(
    // Variables pasadas por entrada, navegación para editar información, comprobación del logeado del usuario
    // y lambda para cerrar sesión en la app
    userIdLogueado: Int,
    onLogout: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToSelectImage: () -> Unit // Navegacion a la pantalla para cambiar la foto de perfil
) {
    // Variables para guardar los datos del usuario
    var usuario by remember { mutableStateOf<UserProfileResponse?>(null) }
    var cargando by remember { mutableStateOf(true) }

    // Llamo a la API al iniciar con el endpoint que obtiene la informació del perfil
    LaunchedEffect(key1 = true) {
        try {
            // Llamo a la API para cargar la información del perfil
            val response = RetrofitClient.apiService.obtenerPerfil(userIdLogueado)
            if (response.isSuccessful) {
                usuario = response.body()
            }
        } catch (e: Exception) {
            println("PRUEBA_PERFIL: Error -> ${e.message}")
        } finally {
            cargando = false
        }
    }

    // Estructura de la página
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Si está cargando la información muestro el circulito
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryLight)
            }
        } else {
            // Si ya ha cargado la información entonces muestro el perfil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen de perfil
                AsyncImage(
                    model = if (usuario?.ImagenPerfil.isNullOrEmpty()) R.drawable.susana else usuario?.ImagenPerfil,
                    contentDescription = "Foto de perfil",
                    placeholder = painterResource(R.drawable.susana),
                    error = painterResource(R.drawable.susana),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable { onNavigateToSelectImage() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = usuario?.NombreUsuario ?: "Usuario",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceLight
                    )
                }

                // Botón para editar la foto
                Button(
                    onClick = { onNavigateToSelectImage() }, // <--- Acción para cambiar foto
                    colors = ButtonDefaults.buttonColors(containerColor = surfaceVariantLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Editar", color = primaryLight, fontWeight = FontWeight.Bold)
                }
            }

            // Surface para montar la card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = surfaceContainerHighLight
            ) {
                // Título de la pantalla
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Perfil",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Cada dato lo mostraré en una fila distinta, se compone de un icono y el dato específico del usuario
                    InfoRow(icon = Icons.Default.DateRange, text = usuario?.FechaNacimiento ?: "--/--/----")
                    InfoRow(icon = Icons.Default.LocationOn, text = usuario?.Ciudad ?: "No especificada")
                    InfoRow(icon = Icons.Default.Email, text = usuario?.Email ?: "Sin email")

                    // Barra horizontal que separa los dos apartados de datos del perfil
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        thickness = 1.dp,
                        color = outlineLight.copy(alpha = 0.2f)
                    )

                    // Opcion de acceso web
                    ActionRow(icon = Icons.Default.Web, text = "Acceso web") { /* Web */ }
                    // Opción de cambiar información
                    ActionRow(
                        icon = Icons.Default.Settings,
                        text = "Cambiar información"
                    ) {
                        // Navegación a la página de modificar información del usuario
                        onNavigateToEdit()
                    }
                    // Opción para cerrar sesión
                    ActionRow(
                        icon = Icons.Default.ExitToApp,
                        text = "Salir",
                        color = Color.Red,
                        onClick = onLogout
                    )
                }
            }
        }
    }
}

// Composable para cada fila  que sea texto informativo del usuario
@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = primaryLight, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 16.sp, color = onSurfaceLight)
    }
}

// Composable para cada fila que sea un botón de acción
@Composable
fun ActionRow(
    icon: ImageVector,
    text: String,
    color: Color = onSurfaceLight,
    onClick: () -> Unit = {}
) {
    // Al igual que el informativo cada uno de ellos será un icono con un texto para
    // informar de que se trata esa opción
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp) // Aumenté un poco el alto para que sea más fácil de tocar
            .clickable { onClick() } // <--- ¡ESTO ES LO QUE FALTA!
            .padding(horizontal = 4.dp), // Un poco de espacio interno
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = color,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f) // Cambiado a weight para que ocupe el resto
        )
    }
}