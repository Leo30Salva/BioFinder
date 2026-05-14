package com.example.biofinderandroid.Documentos

// Imports necesarios

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
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
import com.example.biofinderandroid.R
import com.example.biofinderandroid.ui.theme.*
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

// Función principal del home
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioFinderHome(
    // Parámetros para el home
    especieSeleccionada: String,
    soloRecomendados: Boolean,
    onNavigateToDescripcion: (Animal) -> Unit
) {
    // Variables para la barra de busqueda, la lista de animales, cargando para la pantalla de carga
    // y scope para la ejecución de hilos en segundo plano
    var searchText by remember { mutableStateOf("") }
    var listaAnimales by remember { mutableStateOf<List<Animal>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope() // Necesario para lanzar peticiones desde botones

    // Variables para almacenar el estado de cada uno de los filtros
    var especiesSeleccionadas by remember { mutableStateOf(setOf<String>()) }
    var reproSeleccionada by remember { mutableStateOf(setOf<String>()) }
    var alimSeleccionada by remember { mutableStateOf(setOf<String>()) }
    var incluirExtintos by remember { mutableStateOf(false) }

    // Estado del menú del desplegable
    val sheetState = rememberModalBottomSheetState()
    var showFilters by remember { mutableStateOf(false) }

    // Variable para la carga de los datos
    val cargarDatos = suspend {
        // Por defecto la pantalla de carga estará activada hasta que la información no haya cargado
        cargando = true
        try {
            // Llamada a la API para cargar los filtros de los animales
            val response = RetrofitClient.apiService.filtrarAvanzado(
                especies = when {
                    // Compruebo que el usuario no haya desactivado la preferencia de animales inicial
                    !soloRecomendados -> null
                    // Compruebo si el usuario esta buscando algún animal
                    searchText.isNotEmpty() -> null

                    // Compruebo que el usuario haya elegido en el filtro alguna especie
                    especiesSeleccionadas.isNotEmpty() -> especiesSeleccionadas.joinToString(",")

                    // Muestro la especie seleccionada
                    else -> especieSeleccionada
                },

                // Los demás filtros
                repro = if (reproSeleccionada.isEmpty()) null else reproSeleccionada.joinToString(","),
                alim = if (alimSeleccionada.isEmpty()) null else alimSeleccionada.joinToString(","),
                extinto = incluirExtintos,
                nombre = if (searchText.isEmpty()) null else searchText
            )

            // Cargo la lista de animales
            if (response.isSuccessful) {
                listaAnimales = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            // Error de conexión
        } finally {
            cargando = false
        }
    }
    // Cargo los datos por lo que se haya buscado en el buscador
    LaunchedEffect(searchText, especieSeleccionada, soloRecomendados) {
        cargarDatos()
    }

    // Diseño de la pantalla
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Buscador
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar animal...") },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Botón para el menú de los filtros
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showFilters = true },
                color = primaryLight,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Menu, contentDescription = "Filtros", tint = Color.White)
                }
            }
        }

        // Espaciado con el buscador
        Spacer(modifier = Modifier.height(30.dp))

        // Si esta cargando la información muestro el circulo
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryLight)
            }
        } else {
            // Si ya ha cargado muestro los animales
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Muestro la lista de animales con items
                items(listaAnimales) { animal ->
                    AnimalCard(animal, onDetailClick = { onNavigateToDescripcion(animal) })
                }
            }
        }
    }

    // Los filtros aparecerán en un menú inferior "ModalBottomSheet"
    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = sheetState,
            containerColor = surfaceContainerHighLight
        ) {
            // Los filtros elegidos por el usuario los guardo para mas tarde poder mostrarlo
            FilterMenuContent(
                especiesSel = especiesSeleccionadas,
                onEspeciesChange = { especiesSeleccionadas = it },
                reproSel = reproSeleccionada,
                onReproChange = { reproSeleccionada = it },
                alimSel = alimSeleccionada,
                onAlimChange = { alimSeleccionada = it },
                extinto = incluirExtintos,
                onExtintoChange = { incluirExtintos = it },
                // Al aplicar los cambios cargo los datos con los filtros aplicados
                onApply = {
                    scope.launch {
                        cargarDatos()
                    }
                    showFilters = false
                }
            )
        }
    }
}

// Función para la card de los animales
@Composable
fun AnimalCard(
    animal: Animal,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Muestro de nuevo la imagen con asyncimage al ser una URL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(surfaceVariantLight)
            ) {
                AsyncImage(
                    model = animal.ImagenURL,
                    contentDescription = animal.Nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    error = painterResource(id = R.drawable.ic_launcher_background)
                )
            }

            // El texto se compone por el nombre del animal y el texto de más información
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = animal.Nombre ?: "Sin nombre",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceLight,
                    maxLines = 1
                )
                Text(
                    text = animal.Especie ?: "Desconocida",
                    fontSize = 12.sp,
                    color = outlineLight
                )

                Text(
                    text = "Más información",
                    fontSize = 11.sp,
                    color = primaryLight,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Función del menú de filtros
@Composable
fun FilterMenuContent(
    // Parámetros pasados a la función para montarlo
    especiesSel: Set<String>,
    onEspeciesChange: (Set<String>) -> Unit,
    reproSel: Set<String>,
    onReproChange: (Set<String>) -> Unit,
    alimSel: Set<String>,
    onAlimChange: (Set<String>) -> Unit,
    extinto: Boolean,
    onExtintoChange: (Boolean) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Preferencias", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        // La primera sección es en la que pido los tipos de animales que quiere buscar,defino
        // la lista con todas las opciones
        FilterSection("Tipo de Animal") {
            val tipos = listOf("Mamíferos", "Aves", "Peces", "Insectos", "Reptiles", "Arácnidos", "Anfibios", "Moluscos")
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Para cada especie de animal creo un selectaleiconitem
                tipos.forEach { tipo ->
                    val isSelected = especiesSel.contains(tipo)
                    SelectableIconItem(
                        label = tipo,
                        isSelected = isSelected,
                        onClick = {
                            val nuevoSet = especiesSel.toMutableSet()
                            if (isSelected) nuevoSet.remove(tipo) else nuevoSet.add(tipo)
                            onEspeciesChange(nuevoSet)
                        }
                    )
                }
            }
        }

        // Apartado de reproducción
        FilterSection("Reproducción") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Vuelvo a hacer otra lista con las opciones disponibles
                listOf("Vivíparos", "Ovíparos", "Ovovivíparos").forEach { modo ->
                    val isSelected = reproSel.contains(modo)
                    // Voy creando los items
                    FilterChipItem(
                        label = modo,
                        isSelected = isSelected,
                        onClick = {
                            val nuevoSet = reproSel.toMutableSet()
                            if (isSelected) nuevoSet.remove(modo) else nuevoSet.add(modo)
                            onReproChange(nuevoSet)
                        }
                    )
                }
            }
        }

        // Apartado para elegir el tipo de alimentación
        FilterSection("Alimentación") {
            val dietas = listOf(
                "Herbívoro", "Carnívoro",
                "Omnívoro", "Detritívoro",
                "Parásito", "Frutívoro",
                "Insectívoro", "Carroñero",
                "Piscívoro", "Nectarívoro"
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Organizamos en filas de 2 para que sea más fácil de leer
                dietas.chunked(2).forEach { fila ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Creo una fila para cada item que haya separados en dos columnas distintas
                        fila.forEach { dieta ->
                            CheckboxItem(
                                label = dieta,
                                isChecked = alimSel.contains(dieta),
                                onCheckedChange = { checked ->
                                    val nuevoSet = alimSel.toMutableSet()
                                    if (checked) nuevoSet.add(dieta) else nuevoSet.remove(dieta)
                                    onAlimChange(nuevoSet)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Apartado de extintos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceVariantLight, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simplemente un switch que se puede seleccionar para mostrar animales extintos
            Text("¿Incluir especies extintas?", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Switch(
                checked = extinto,
                onCheckedChange = onExtintoChange,
                colors = SwitchDefaults.colors(checkedThumbColor = primaryLight)
            )
        }

        // Botón para guardar los filtros
        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Aplicar Filtros", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Texto para el título de cada apartado
@Composable
fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = outlineLight, fontSize = 16.sp)
        content()
    }
}

// Item para las distintas especies
@Composable
fun SelectableIconItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) primaryLight.copy(alpha = 0.1f) else surfaceVariantLight,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) primaryLight else Color.Transparent),
        modifier = Modifier.size(85.dp) // Tamaño ideal para iconos
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo), // O Icons.Default.Pets
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) primaryLight else outlineLight
            )
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) primaryLight else onSurfaceLight)
        }
    }
}

// Chip Item para los distintos tipos de reproducción
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp) },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = primaryLight,
            selectedLabelColor = Color.White
        )
    )
}

// CheckboxItem para los distintos tipos de alimentación
@Composable
fun CheckboxItem(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(checkedColor = primaryLight))
        Text(label, fontSize = 14.sp)
    }
}