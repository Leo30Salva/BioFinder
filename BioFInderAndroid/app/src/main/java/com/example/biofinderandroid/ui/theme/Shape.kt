package com.example.biofinderandroid.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // Para elementos pequeños como los campos de texto (TextField)
    // y el banner de seguridad (antes tenías 12.dp)
    small = RoundedCornerShape(12.dp),

    // Para botones y fotos de animales en la grid (antes tenías 16.dp o 24.dp)
    medium = RoundedCornerShape(16.dp),

    // Para las tarjetas principales (Surface) de tus pantallas
    // de Registro, Login y Ajustes (antes tenías 32.dp)
    large = RoundedCornerShape(32.dp)
)