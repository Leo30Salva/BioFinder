package com.example.biofinderandroid.Documentos

// Imports necesarios

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.biofinderandroid.R
import com.example.biofinderandroid.ui.theme.backgroundLight
import com.example.biofinderandroid.ui.theme.onSurfaceVariantLight
import com.example.biofinderandroid.ui.theme.surfaceVariantLight
// Función principal del historial
@Composable
fun BioFinderHistorial(
    userId: Int,
    onNavigateToDescripcion: (Animal) -> Unit
) {
    // Variable para el circulo de carga
    var cargando by remember { mutableStateOf(true) }
    // Variable del historial de animales, que será una lista de animales
    var historialAnimales by remember { mutableStateOf<List<Animal>>(emptyList()) }

    // LanchedEffect para llamar a la API y lanzar el endpoint para obtener el historial
    // del usuario
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.obtenerHistorial(userId)
            if (response.isSuccessful) {
                historialAnimales = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("API", "Error: ${e.message}")
        } finally {
            cargando = false
        }
    }

    // Si esta cargando muestro el circulo de carga en la pantalla
    if (cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // En el caso de que ya haya cargado muestro los animales en el historial
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Muestro los items del historial en cards
            items(historialAnimales) { animal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToDescripcion(animal) }
                ) {
                    // De nuevo utilizo asyncimage ya que vienen de enlaces las imágenes
                    Column {
                        AsyncImage(
                            model = animal.ImagenURL ?: "",
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentScale = ContentScale.Crop
                        )
                        // Nombre del animal
                        Text(
                            text = animal.Nombre ?: "Desconocido",
                            modifier = Modifier.padding(8.dp),
                            fontWeight = FontWeight.Bold
                        )
                        // Muestro la especie del animal
                        Text(
                            text = "Visto recientemente",
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}