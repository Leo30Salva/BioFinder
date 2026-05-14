package com.example.biofinderandroid.Documentos

// Imports necesarios
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.example.biofinderandroid.ui.theme.primaryLight
import com.example.biofinderandroid.ui.theme.surfaceContainerHighLight

// Data class para organizar el contenido del footer
data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// Función principal del footer
@Composable
fun BioFinderFooter(
    pantallaActual: String,
    onNavigate: (String) -> Unit,
    mostrarFavoritos: Boolean,
    mostrarHistorial: Boolean
) {
    // Items del footer
    val allItems = listOf(
        NavigationItem("Inicio", Icons.Default.Home, "Home"),
        NavigationItem("Perfil", Icons.Default.Person, "Perfil"),
        NavigationItem("Ajustes", Icons.Default.Settings, "Ajustes"),
        NavigationItem("Favoritos", Icons.Default.Star, "Favoritos"),
        NavigationItem("Historial", Icons.Default.Search, "Historial")
    )

    // Filtrado de los items favoritos e historial, ya que el usuario
    // puede elegir desactivarlos en la página de ajustes
    val itemsVisibles = allItems.filter { item ->
        when (item.route) {
            "Favoritos" -> mostrarFavoritos
            "Historial" -> mostrarHistorial
            else -> true // El else para todos los demás items, que siempre seran true
            // por lo que siempre serán visibles
        }
    }

    // EL navigation bar donde aplicaré todo lo anterior
    NavigationBar(
        // Defino con los colores de mi material3
        containerColor = surfaceContainerHighLight,
        contentColor = primaryLight
    ) {
        // Hago un foreach para mostrar todos los items del bottom bar
        itemsVisibles.forEach { item ->
            // Para cada item, pongo su icono, texto, color y que sea clickable
            NavigationBarItem(
                selected = pantallaActual == item.route,
                onClick = { onNavigate(item.route) },
                label = { Text(text = item.label, fontSize = 10.sp) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryLight,
                    selectedTextColor = primaryLight,
                    indicatorColor = primaryLight.copy(alpha = 0.1f)
                )
            )
        }
    }
}