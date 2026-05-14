// Me conecto a la API
const API_BASE_URL = 'http://127.0.0.1:8000'; 
let urlSeleccionada = "";

// Obtengo el ID del usuario desde el almacenamiento local
const userId = localStorage.getItem("userId");

// Referencia al grid de animales y al boton para cambiar la foto
const gridAnimales = document.getElementById('gridAnimalesCambiarFoto');
const btnCambiarFoto = document.getElementById('botonCambiarFoto');

// Cargo la lista entera de animales
async function cargarAnimales() {
    try {
        // Llamo al endpoint de la API que llama a todos los animales
        const response = await fetch(`${API_BASE_URL}/animales`);
        
        if (!response.ok) throw new Error("No se pudieron obtener los animales");
        
        const animales = await response.json();
        
        gridAnimales.innerHTML = "";

        // Para cargar los animales
        animales.forEach(animal => {
            if (animal.ImagenURL) {
                const img = document.createElement('img');
                img.src = animal.ImagenURL;
                img.alt = animal.NombreAnimal || "Animal";
                img.classList.add('elegirFoto');
                
                // Evento al hacer clic en una foto
                img.addEventListener('click', () => {
                    gestionarSeleccionVisual(img, animal.ImagenURL);
                });

                gridAnimales.appendChild(img);
            }
        });
    } catch (error) {
        console.error("Error:", error);
        gridAnimales.innerHTML = "<p>Error al cargar las imágenes.</p>";
    }
}


// Cambio visual al poner el raton encima de la imagen
function gestionarSeleccionVisual(elemento, url) {

    document.querySelectorAll('.elegirFoto').forEach(foto => {
        foto.classList.remove('seleccionada');
    });

    elemento.classList.add('seleccionada');
    
    urlSeleccionada = url;
    btnCambiarFoto.disabled = false;
}


// Envio la imagen elegida a la base de datos
async function actualizarFoto() {
    if (!urlSeleccionada || !userId) {
        alert("Falta información del usuario o de la imagen");
        return;
    }

    try {

        // Envio la peticion al endpoint de la API para cambiar la foto
        const urlPeticion = `${API_BASE_URL}/usuarios/${userId}/foto?nueva_url=${encodeURIComponent(urlSeleccionada)}`;

        const response = await fetch(urlPeticion, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Si sale correctamente redirecciono a la página de perfil
        if (response.ok) {
            alert("¡Foto de perfil actualizada con éxito!");
            window.location.href = "Perfil.html"; 
        } else {
            const errorData = await response.json();
            alert("Error: " + (errorData.detail || "No se pudo actualizar"));
        }
    } catch (error) {
        console.error("Error en la petición:", error);
        alert("Error de conexión con el servidor.");
    }
}

// Listener al boton de cambiar la foto
btnCambiarFoto.addEventListener('click', actualizarFoto);

// Ejecutar la carga al iniciar la página
document.addEventListener('DOMContentLoaded', cargarAnimales);
