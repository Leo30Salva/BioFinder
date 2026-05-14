package com.example.biofinderandroid.Documentos

// Imports necesarios

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biofinderandroid.ui.theme.*

@Composable
fun BioFinderRecomendaciones( onNavigateToHome: (String) -> Unit) {
    // Lista de especies
    val especies = listOf(
        "Mamíferos", "Aves", "Peces", "Reptil",
        "Anfibios", "Insectos", "Arácnidos", "Moluscos"
    )

    // Donde se almacenará la especie seleccionada
    var opcionSeleccionada by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Columna principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLight)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Objeto donde irán todos los elementos
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            color = surfaceContainerHighLight // Tu color de tarjeta
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Texto del tiulo del apartado
                Text(
                    text = "¿Qué animales prefieres?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceLight,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Utilizo la lista de opciones que hice anteriormente y hago un foreach
                // para mostrar uno a uno
                especies.forEach { especie ->
                    AnimalOptionRow(
                        nombre = especie,
                        seleccionado = (especie == opcionSeleccionada),
                        onSelect = { opcionSeleccionada = especie }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón para continuar y acceder al home
                Button(
                    onClick = {
                        if (opcionSeleccionada.isNotEmpty()) {
                            // Paso la especie seleccionada a la navegación hacia el home
                            onNavigateToHome(opcionSeleccionada)
                            // Informo que la preferencia de animal del usuario fue guardada
                            Toast.makeText(context, "Preferencia guardada: $opcionSeleccionada", Toast.LENGTH_SHORT).show()
                        } else {
                            // Si el botón es seleccionado y ninguna opción esta seleccionada informo
                            Toast.makeText(context, "Por favor, elige una especie", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    // Texto del botón
                    Text(
                        text = "Continuar",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Para cada animal creo una fila dentro de la columna principal, con el nombre de la opción
// y su respectivo radiobutton
@Composable
fun AnimalOptionRow(
    nombre: String,
    seleccionado: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Estructura del texto y el radiobutton
        Text(
            text = nombre,
            fontSize = 18.sp,
            color = if (seleccionado) primaryLight else onSurfaceLight,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
        )

        RadioButton(
            selected = seleccionado,
            onClick = { onSelect() },
            colors = RadioButtonDefaults.colors(
                selectedColor = primaryLight,
                unselectedColor = outlineLight
            )
        )
    }
}