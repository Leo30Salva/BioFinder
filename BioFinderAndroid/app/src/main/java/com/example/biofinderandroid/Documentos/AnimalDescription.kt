package com.example.biofinderandroid.Documentos

// Imports necesarios para la pagina de descripción de cada animal

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.biofinderandroid.R
import com.example.biofinderandroid.ui.theme.*
import kotlinx.coroutines.launch

// Función principal de la página de detalle
@Composable
fun BioFinderDetalle(animal: Animal, userIdLogueado: Int, onBack: () -> Unit) {

    // Variable que almacena si el usuario tiene o no el animal en favoritos
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    // Scope para conectarse con la API
    val scope = rememberCoroutineScope()

    // Disparador para guardar en el historial
    LaunchedEffect(Unit) {
        // Verifico que el usuario esté logeado
        if (userIdLogueado != -1) {
            // Llamo a la función de la API que guarde el animal en el historial
            try {
                RetrofitClient.apiService.registrarHistorial(userIdLogueado, animal.id)
            } catch (e: Exception) {}
        }
    }

    // Toda la información del animal la meto en una caja
    Box(modifier = Modifier.fillMaxSize().background(backgroundLight)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // La imagen donde irá el animal
            AsyncImage(
                model = animal.ImagenURL ?: "",
                contentDescription = animal.Nombre,
                modifier = Modifier.fillMaxWidth().height(320.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.logo),
                error = painterResource(id = R.drawable.logo)
            )

            // Todo lo demas lo meto en un surface donde se vera la información
            Surface(
                modifier = Modifier.fillMaxWidth().offset(y = (-40).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = backgroundLight
            ) {
                // Columna principal
                Column(modifier = Modifier.padding(20.dp)) {

                    // Nombre del animal
                    Text(animal.Nombre ?: "Detalle", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = primaryLight)
                    Spacer(modifier = Modifier.height(12.dp))
                    // Cards rectangulares con informacion breve
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoStatCard(Modifier.weight(1f), R.drawable.activitieicon, "Actividad", animal.Actividad ?: "N/A")
                        InfoStatCard(Modifier.weight(1f), R.drawable.cientificicon, "Nombre Científico", animal.NombreCientifico ?: "Desconocido")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cards cuadrados con informacion breve
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MiniIconCard(Modifier.weight(1f), R.drawable.footprint, "Especie", animal.Especie ?: "-", Color(0xFFE1BEE7))
                        MiniIconCard(Modifier.weight(1f), R.drawable.lifeicon, "Vida", animal.EsperanzaVida ?: "-", Color(0xFFC5CAE9))
                        MiniIconCard(Modifier.weight(1f), R.drawable.foodicon, "Alimentación", animal.Alimentacion ?: "-", Color(0xFFFFF9C4))
                        MiniIconCard(Modifier.weight(1f), R.drawable.extintoicon, "Extinto", if (animal.Extinto == true) "Sí" else "No", Color(0xFFF8BBD0))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Parte donde va el mapa de street map
                    Text("Ubicación", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = onSurfaceLight)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Box donde irá ubicado el mapa de street map
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.LightGray)
                    ) {
                        // Android view permite visualizar XML en compose
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )

                                    setOnTouchListener { view, event ->
                                        // Con esto no se moverá la pantalla mientras se scrollea en la pantalla
                                        view.parent.requestDisallowInterceptTouchEvent(true)
                                        false
                                    }
                                    // Parametros que se le pasará a street map
                                    webViewClient = WebViewClient()
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.userAgentString = "BioFinderAndroidApp_v1_0_Estudiante"
                                    settings.setSupportZoom(true)
                                    settings.builtInZoomControls = true
                                    settings.displayZoomControls = false

                                    val html = generateMapHtml(animal.Ubicacion, animal.Nombre ?: "Animal")
                                    loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Descripción del animal
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Descripción", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = onSurfaceLight)
                    Text(
                        text = animal.Descripcion ?: "Sin descripción disponible.",
                        fontSize = 15.sp,
                        color = outlineLight,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = outlineLight.copy(alpha = 0.2f))

                    // Fila donde separo en dos columnas el tipo de reproducción
                    // y si está o no en favoritos
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Columna para el apartado de reproducción del animal
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Reproducción", fontSize = 14.sp, color = outlineLight)
                            Text(animal.Reproduccion ?: "-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        // Línea que divide las dos columnas
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(outlineLight.copy(alpha = 0.3f))
                        )

                        // Columna de favoritos
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Favorito", fontSize = 14.sp, color = outlineLight)

                            IconButton(
                                onClick = {
                                    // Si el usuario lo añade a favoritos llamo a la API
                                    scope.launch {
                                        try {
                                            if (isFavorite) {
                                                // Si ya es favorito, lo eliminamos
                                                val resp = RetrofitClient.apiService.eliminarFavorito(userIdLogueado, animal.id)
                                                if (resp.isSuccessful) isFavorite = false
                                            } else {
                                                // Si no es favorito, lo añadimos
                                                val resp = RetrofitClient.apiService.agregarFavorito(userIdLogueado, animal.id)
                                                if (resp.isSuccessful) isFavorite = true
                                            }
                                        } catch (e: Exception) {

                                        }
                                    }
                                }
                            ) {
                                // Icono de favoritos
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isFavorite) Color.Red else outlineLight,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Flecha para echar hacia atras fija en la pantalla
        FilledIconButton(
            onClick = onBack,
            modifier = Modifier.padding(16.dp).size(45.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = backgroundLight.copy(alpha = 0.9f))
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    }
}

// Función que genera el mapa HTML y lo carga en la pantalla
fun generateMapHtml(coordenadas: String?, nombre: String): String {
    if (coordenadas.isNullOrBlank() || coordenadas == "[]" || coordenadas == "null") {
        return "<html><body style='display:flex;justify-content:center;align-items:center;height:100vh;font-family:sans-serif;'><h3>Ubicación no disponible</h3></body></html>"
    }

    val limpio = coordenadas.replace("[", "").replace("]", "").replace("\"", "").trim()
    val partes = limpio.split(",")
    val lat = partes.getOrNull(0)?.trim() ?: "0.0"
    val lon = partes.getOrNull(1)?.trim() ?: "0.0"

    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                #map { height: 100vh; width: 100vw; margin: 0; padding: 0; background: #e0e0e0; }
                body { margin: 0; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                try {
                    var map = L.map('map', { zoomControl: false }).setView([$lat, $lon], 4);
                    
                    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
                        attribution: '&copy; OpenStreetMap'
                    }).addTo(map);

                    L.marker([$lat, $lon]).addTo(map)
                        .bindPopup('<b>$nombre</b>')
                        .openPopup();
                } catch (e) {
                    document.body.innerHTML = "<h3>Error: " + e.message + "</h3>";
                }
            </script>
        </body>
        </html>
    """.trimIndent()
}

// Función para crear los cards cuadrados de los datos del animal
@Composable
fun MiniIconCard(modifier: Modifier, imageRes: Int, label: String, value: String, bgColor: Color) {
    // Todo dentro de una columna
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(surfaceVariantLight).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Solamente está compuesto por una imagen que será un icono y un texto
        Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.size(20.dp))
        Text(label, fontSize = 9.sp, color = outlineLight)
        Surface(
            modifier = Modifier.padding(top = 4.dp).padding(horizontal = 4.dp),
            shape = RoundedCornerShape(8.dp),
            color = bgColor
        ) {
            Text(value, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), textAlign = TextAlign.Center)
        }
    }
}

// La card rectangular
@Composable
fun InfoStatCard(modifier: Modifier, imageRes: Int, label: String, value: String) {
    // Dentro de un surface creo una fila donde irá una imagen y una columna al igual que la anterior
    Surface(modifier = modifier, shape = RoundedCornerShape(16.dp), color = surfaceVariantLight, border = BorderStroke(1.dp, outlineLight.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(label, fontSize = 10.sp, color = outlineLight)
                Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}