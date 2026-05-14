package com.example.biofinderandroid.Documentos

// Imports necesarios

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.biofinderandroid.ui.theme.primaryLight
import kotlinx.coroutines.launch

// Función para elegir foto de perfil
@Composable
fun BioFinderSeleccionImagen(
    userIdLogueado: Int,
    onNavigateBack: () -> Unit
) {
    // Variable para el mensaje toast y scope para llamar a la API
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Variables para la lista de animales y la imagen seleccionada
    var listaAnimales by remember { mutableStateOf<List<Animal>>(emptyList()) }
    var imagenSeleccionadaUrl by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }

    // Carga todos los animales al iniciar para mostrar sus fotos
    LaunchedEffect(Unit) {
        try {
            // Llamo al endpoint
            val response = RetrofitClient.apiService.obtenerAnimales()
            if (response.isSuccessful) {
                listaAnimales = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar imágenes", Toast.LENGTH_SHORT).show()
        } finally {
            cargando = false
        }
    }

    // Estructura de la página
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Text(
            text = "Elige tu foto de perfil",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Caja con todas las fotos de los animales
        if (cargando) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 fotos por fila
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cada item será un animal reflejado en la pantalla
                items(listaAnimales) { animal ->
                    val esSeleccionada = imagenSeleccionadaUrl == animal.ImagenURL

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (esSeleccionada) 4.dp else 0.dp,
                                color = if (esSeleccionada) primaryLight else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { imagenSeleccionadaUrl = animal.ImagenURL ?: "" }
                    ) {
                        AsyncImage(
                            model = animal.ImagenURL,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar la nueva foto de perfil
        Button(
            onClick = {
                // Si se ha seleccionado alguna imagen entonces llamo al endpoint de la API
                if (imagenSeleccionadaUrl.isNotEmpty()) {
                    scope.launch {
                        try {
                            // Llamamos al nuevo endpoint
                            val response = RetrofitClient.apiService.actualizarFotoPerfil(
                                userId = userIdLogueado,
                                url = imagenSeleccionadaUrl
                            )

                            // Si el cambio se realiza correctamente informo
                            if (response.isSuccessful) {
                                Toast.makeText(context, "¡Imagen de perfil cambiada!", Toast.LENGTH_SHORT).show()
                                onNavigateBack() // Volvemos al perfil para ver el cambio
                            } else {
                                Toast.makeText(context, "Error al guardar en BD", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            enabled = imagenSeleccionadaUrl.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
            containerColor = primaryLight,
            contentColor = Color.White
        )
        ) {
            // Texto del botón
            Text("Cambiar foto", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}