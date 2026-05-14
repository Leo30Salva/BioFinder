package com.example.biofinderandroid.Documentos

// Pongo todos los imports necesarios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biofinderandroid.ui.theme.*
import kotlinx.coroutines.launch


// Función principal de esta página de ajustes
@Composable
fun BioFinderAjustes(
    // Parámetros de entrada de la función, todos estos valores serán pasados desde el main
    userId: Int,
    prefFavoritos: Boolean,
    prefHistorial: Boolean,
    prefRecomendados: Boolean,
    onPrefsChanged: (Boolean, Boolean, Boolean) -> Unit,
    onDeleteAccount: () -> Unit
) {

    // Variable para acceder a la API desde otro hilo diferente para que la pantalla no quede congelada
    val scope = rememberCoroutineScope()
    // Texto informativo parala opción de borrado de cuenta
    var mostrarDialogoBorrar by remember { mutableStateOf(false) }

    // Uso estados locales que se sincronizan con los globales cuando se pulsa el botón de
    // guardar los cambios
    var fav by remember { mutableStateOf(prefFavoritos) }
    var hist by remember { mutableStateOf(prefHistorial) }
    var rec by remember { mutableStateOf(prefRecomendados) }

    // Columna donde se encuentran todos los elementos
    Column(modifier = Modifier.fillMaxSize().background(backgroundLight).padding(16.dp).verticalScroll(rememberScrollState())) {
        // Espaciado para separarlo del header
        Spacer(modifier = Modifier.height(70.dp))
        // Creo la card donde irán todos los elementos
        Surface(shape = RoundedCornerShape(24.dp), color = surfaceContainerHighLight) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Texto del titulo
                Text(
                    text = "Configura tus preferencias",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryLight
                )
                // Switchs de la pagina
                AjusteSwitchRow("Mostrar favoritos", fav) { fav = it }
                AjusteSwitchRow("Mostrar historial", hist) { hist = it }
                AjusteSwitchRow("Mostrar recomendados", rec) { rec = it }

                Spacer(modifier = Modifier.height(32.dp))

                // Boton para borrar la cuenta
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Borrar cuenta"    , color = onSurfaceLight)
                    Button(
                        // Si se clicka se mostrará un texto de advertencia previamente
                        onClick = { mostrarDialogoBorrar = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
                    ) { Text("Borrar", color = Color.White) }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Botón para guardar los cambios
                Button(
                    onClick = { onPrefsChanged(fav, hist, rec) },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight)
                ) { Text("Guardar cambios", color = Color.White) }
            }
        }
    }

    // Texto para la confirmación del diálogo
    if (mostrarDialogoBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrar = false },
            title = { Text("¿Eliminar cuenta?") },
            text = { Text("Esta acción no se puede deshacer. Se borrarán tus favoritos e historial.") },
            confirmButton = {
                TextButton(onClick = {
                    // En el caso de que se decida borrar creo otro hilo secundario
                    // y llama al endpoint de eliminarUsuario
                    scope.launch {
                        val resp = RetrofitClient.apiService.eliminarUsuario(userId)
                        if (resp.isSuccessful) {
                            mostrarDialogoBorrar = false
                            onDeleteAccount()
                        }
                    }
                }) { Text("Confirmar", color = Color.Red) }
            },
            // Botón de cancelar la operación
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBorrar = false }) { Text("Cancelar") }
            }
        )
    }
}

// Componente de la fila para cada switch
@Composable
fun AjusteSwitchRow(
    // Parámetros que se le pasará para crear cada uno de los switch, incluyendo una función lambda
    // para poder almacenar ese estado del switch
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Fila que contiene los componentes del switch
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = onSurfaceLight
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryLight,
                uncheckedThumbColor = outlineLight,
                uncheckedTrackColor = surfaceVariantLight
            )
        )
    }
}