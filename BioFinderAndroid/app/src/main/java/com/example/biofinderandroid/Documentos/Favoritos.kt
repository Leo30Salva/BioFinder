package com.example.biofinderandroid.Documentos

// Imports necesarios

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.biofinderandroid.ui.theme.*
import kotlinx.coroutines.launch

// Función principal de favoritos
@Composable
fun BioFinderFavoritos(userId: Int, onNavigateToDescripcion: (Animal) -> Unit) {
    // Variables necesarias, la lista de favoritos y el scope para llamar a la API en un hilo secundario
    var listaFavoritos by remember { mutableStateOf<List<Animal>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // // Con launched effect llamo a la API para que cargue los favoritos del usuario
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.obtenerFavoritos(userId)
            // Si todo sale bien devuelve la lista de favoritos del usuario
            if (response.isSuccessful) {
                listaFavoritos = response.body() ?: emptyList()
            }
        } catch (e: Exception) {} finally {
            cargando = false
        }
    }

    // Columna principal
    Column(modifier = Modifier.fillMaxSize().background(backgroundLight).padding(16.dp)) {
        Text("Animales favoritos", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))

        // Si está cargando muestro un circulo durante la carga
        if (cargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryLight)
            }
            // Si el usuario no tiene animales en favoritos se lo muestro con un texto
        } else if (listaFavoritos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes animales favoritos.", color = outlineLight)
            }
        } else {
            // Si tiene aimales en favoritos los muestro en un lazyverticalgrid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Muestro la lista de favoritos con items, creo uno para cada id de animal
                // que haya en favoritos para el usuario concreto
                items(listaFavoritos, key = { it.id }) { animal ->
                    FavoriteAnimalCard(
                        animal = animal,
                        //  Si se desea eliminar el animal de favoritos
                        onRemove = {
                            // Llamo a la función de la API que borra al animal de favoritos
                            scope.launch {
                                try {
                                    val resp = RetrofitClient.apiService.eliminarFavorito(userId, animal.id)
                                    if (resp.isSuccessful) {
                                        // Si sale bien recargo la pantalla para que no salga el animal eliminado
                                        listaFavoritos = listaFavoritos.filter { it.id != animal.id }
                                    }
                                } catch (e: Exception) {

                                }
                            }
                        },
                        // Si se clicka al animal navega a la descripción del animal
                        onClick = { onNavigateToDescripcion(animal) }
                    )
                }
            }
        }
    }
}

// Card para los animales
@Composable
fun FavoriteAnimalCard(animal: Animal, onRemove: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundLight)
    ) {
        // En la columna pongo la imagen con async, ya que es mejor para imagenes con URL
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.2f).clip(RoundedCornerShape(24.dp))) {
                AsyncImage(
                    model = animal.ImagenURL,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.logo)
                )

                // El botón que contiene el icono del corazón para eliminar de favoritos en cuanto se pulse
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
            }
            // Texto que contendrá el nombre del animal
            Text(
                animal.Nombre ?: "Sin nombre",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}