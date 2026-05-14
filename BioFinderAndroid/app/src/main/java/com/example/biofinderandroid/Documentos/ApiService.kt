package com.example.biofinderandroid.Documentos

// Imports necesarios

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Tabla del usuario
data class UserRegisterRequest(
    val NombreUsuario: String,
    val Email: String,
    val FechaNacimiento: String,
    val Ciudad: String,
    val Password: String
)

// Datos con los que se dará una respuesta al usuario
data class UserResponse(
    val NombreUsuario: String,
    val Email: String
)

// Datos para realizar el login, solo email y contraseña
data class UserLoginRequest(
    val Email: String,
    val Password: String
)

// Datos con los que se responderá al usuario al logearse
data class LoginResponse(
    val mensaje: String,
    val nombre: String,
    val id: Int
)

// Para cargar los datos en el perfil
data class UserProfileResponse(
    val NombreUsuario: String,
    val Email: String,
    val FechaNacimiento: String,
    val Ciudad: String,
    val ImagenPerfil: String?
)

// Toda la información del animal para que se pueda mostrar en la pantalla de descripcion
data class Animal(
    @SerializedName("IdAnimal") val id: Int,
    @SerializedName("NombreAnimal") val Nombre: String?,
    @SerializedName("Especie") val Especie: String?,
    @SerializedName("Descripcion") val Descripcion: String?,
    @SerializedName("ImagenURL") val ImagenURL: String?,
    @SerializedName("Actividad") val Actividad: String?,
    @SerializedName("NombreCientifico") val NombreCientifico: String?,
    @SerializedName("EsperanzaVida") val EsperanzaVida: String?,
    @SerializedName("TipoAlimentacion") val Alimentacion: String?,
    @SerializedName("Reproduccion") val Reproduccion: String?,
    @SerializedName("Extinto") val Extinto: Boolean?,
    @SerializedName("Ubicacion") val Ubicacion: String?
)


// Mensaje informativo para saber si el animal pasó a favoritos correctamente
data class FavoritoResponse(
    val mensaje: String,
    @SerializedName("id_favorito") val idFavorito: Int? = null
)

// Información para realizar el historial de cada usuario

data class HistorialItem(
    @SerializedName("idHistorial") val idHistorial: Int?,
    @SerializedName("UltimaConsulta") val fecha: String?,
    @SerializedName("CantidadConsultas") val cantidad: Int?,
    @SerializedName("Animal") val datosAnimal: Animal?
)

// La interfaz de la API
interface BioFinderApiService {
    // Post que accede a a la API para registrar un usuario
    @POST("registro")
    suspend fun registrarUsuario(@Body datos: UserRegisterRequest): Response<UserResponse>

    // Post de login que accede a la API para validar un usuario que se logea
    @POST("login")
    suspend fun loginUsuario(@Body credenciales: UserLoginRequest): Response<LoginResponse>

    // Endpoint GET para filtrar animales por especie
    @GET("animales/filtrar")
    suspend fun filtrarAnimales(
        @Query("especie") especie: String
    ): Response<List<Animal>>

    // GET para acceder a la información del perfil
    @GET("usuario/{user_id}")
    suspend fun obtenerPerfil(
        @Path("user_id") userId: Int
    ): Response<UserProfileResponse>

    // Get para el filtrado de animales por varios campos ingresados
    @GET("animales/filtrar_avanzado")
    suspend fun filtrarAvanzado(
        @Query("especies") especies: String? = null,
        @Query("reproduccion") repro: String? = null,
        @Query("alimentacion") alim: String? = null,
        @Query("extinto") extinto: Boolean? = null,
        @Query("nombre") nombre: String? = null
    ): Response<List<Animal>>

    // PUT para modificar la información de un usuario
    @PUT("usuario/actualizar/{user_id}")
    suspend fun actualizarUsuario(
        @Path("user_id") userId: Int,
        @Body datos: UserRegisterRequest
    ): Response<Map<String, String>>

    // Post para agregar un animal a favoritos
    @POST("favoritos/agregar")
    suspend fun agregarFavorito(
        @Query("user_id") userId: Int,
        @Query("animal_id") animalId: Int
    ): Response<FavoritoResponse>

    // Get para mostrar los favoritos de cada usuario específico
    @GET("favoritos/usuario/{user_id}")
    suspend fun obtenerFavoritos(
        @Path("user_id") userId: Int
    ): Response<List<Animal>>

    // DELETE para eliminar un animal de favoritos
    @DELETE("favoritos/eliminar")
    suspend fun eliminarFavorito(
        @Query("user_id") userId: Int,
        @Query("animal_id") animalId: Int
    ): Response<FavoritoResponse>

    // Post para añadir un animal al historial que haya sido consultado por un usuario
    @POST("historial/registrar")
    suspend fun registrarHistorial(
        @Query("user_id") userId: Int,
        @Query("animal_id") animalId: Int
    ): Response<Map<String, String>>

    // Get para que cada usuario tenga su historial individual
    @GET("historial/{user_id}")
    suspend fun obtenerHistorial(@Path("user_id") userId: Int): Response<List<Animal>>

    // DELETE para eliminar un usuario
    @DELETE("usuario/eliminar/{user_id}")
    suspend fun eliminarUsuario(@Path("user_id") userId: Int): Response<Map<String, String>>

    // GET para obtener todos los animales
    @GET("animales")
    suspend fun obtenerAnimales(): Response<List<Animal>>

    // PUT para cambiar la foto de perfil
    @PUT("usuarios/{id}/foto")
    suspend fun actualizarFotoPerfil(
        @Path("id") userId: Int,
        @Query("nueva_url") url: String
    ): Response<ResponseBody>
}

// Conexión a la API
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val apiService: BioFinderApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BioFinderApiService::class.java)
    }
}