package com.example.biofinderandroid.Documentos

// Pongo todos los imports

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biofinderandroid.R
import com.example.biofinderandroid.ui.theme.onSurfaceLight
import com.example.biofinderandroid.ui.theme.primaryLight
import androidx.compose.material3.Surface
import com.example.biofinderandroid.ui.theme.backgroundLight
import com.example.biofinderandroid.ui.theme.surfaceContainerHighLight

// Composable para el header
@Composable
fun BioFinderHeader(tituloSeccion: String) {
    // Hago el header con una estructura de surface
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = surfaceContainerHighLight,
        shadowElevation = 8.dp // Le pongo una pequeña sombra
    ) {
        // Fila principal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Ajusto el padding del header
                .padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            // Lo alineo todo al centro verticalmente
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo y nombre de la app
            // Uso weight(1f) para que ocupe espacio a la izquierda y permita centrar el resto
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                // Lo divido en una columna para poner el texto debajo de la imagen del logo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo BioFinder",
                        modifier = Modifier.size(100.dp) // Tamaño ajustado para no romper el header
                    )
                    Text(
                        text = "BioFinder",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryLight
                    )
                }
            }

            // Texto del titulo de la página
            Text(
                text = tituloSeccion,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.wrapContentSize()
            )

            // Con este box aseguro que no se vaya hacia la izquierda mi título de página
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
            }
        }
    }
}