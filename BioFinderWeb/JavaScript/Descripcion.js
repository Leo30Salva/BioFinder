document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const animalId = params.get('id');
    const checkFavorito = document.getElementById('checkFavorito');

    const userId = localStorage.getItem('usuariosId'); 
    
    // Actualizo el historial del usuario cuando el usuario entra a la página de descripción del animal
    if (userId && animalId) {
        fetch(`http://127.0.0.1:8000/historial/registrar?user_id=${userId}&animal_id=${animalId}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => console.log("Historial actualizado:", data))
        .catch(error => console.error('Error al registrar historial:', error));
    }

    if (!animalId) return;

    // Muestro la pantalla del animal que el usuario haya seleccionado con el método creado "animales({animalId})"
    try {
        // Conecto con la API
        const respuesta = await fetch(`http://127.0.0.1:8000/animales/${animalId}`);
        const animal = await respuesta.json();

        // Referencia a los ID de cada campo para rellenar la pantalla de cada animal
        document.querySelector(".detailsAnimalName").textContent = animal.NombreAnimal;
        document.getElementById("imgAnimal").src = animal.ImagenURL;
        
        document.getElementById("valActividad").textContent = animal.Actividad;
        document.getElementById("valNombreCientifico").textContent = animal.NombreCientifico;
        document.getElementById("valEspecie").textContent = animal.Especie;
        
        document.getElementById("valVida").textContent = animal.EsperanzaVida;
        document.getElementById("valAlim").textContent = animal.TipoAlimentacion;
        
        document.getElementById("valExtinto").textContent = animal.Extinto ? "Sí" : "No";

        document.getElementById("descTexto").textContent = animal.Descripcion;
        document.getElementById("reproTexto").textContent = animal.Reproduccion;

        // Si el animal tiene ubicaciones las muestro
        if (animal.Ubicacion) {
            try {
                const coords = JSON.parse(animal.Ubicacion); 

                // El mapa será centrado en la primera ubicación del array
                const map = L.map('map').setView(coords[0], 4);

                // Añado la ruta al street map 
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    attribution: '&copy; OpenStreetMap'
                }).addTo(map);

                setTimeout(() => {
                map.invalidateSize();
                }, 100);

                // Marco cada coordenada en el mapa
                coords.forEach(punto => {
                    L.marker(punto).addTo(map);
                });
            } catch (e) {
                console.error("Error al parsear coordenadas o cargar mapa:", e);
                // Si falla el mapa muestro un texto informando que hubo un error al cargar las coordenadas
                document.getElementById("map").textContent = "Error al cargar las coordenadas.";
            }
        }


        // Estructura para la pantalla de favoritos
        if (checkFavorito) {
            checkFavorito.addEventListener('change', async () => {
                const params = new URLSearchParams(window.location.search);
                const animalId = params.get('id');
                
                // Cojo la referencia del ID del usuario para saber que usuario es el que esta guardando ese animal en favoritos
                const userId = localStorage.getItem('usuariosId'); 

                // Si no se encuentra la referencia al ID de usuario o animal le indico que es necesario que inicie
                // sesión para poder guardarle en la pantalla de favoritos al respectivo animal
                if (!userId || !animalId) {
                    alert("Inicia sesión para guardar favoritos");
                    checkFavorito.checked = false;
                    return;
                }

                // Si el checkbox ha sido pulsado llamo a la API con la función correspondiente creada para añadirlo a favoritos
                if (checkFavorito.checked) {
                    try {
                        const url = `http://127.0.0.1:8000/favoritos/agregar?user_id=${userId}&animal_id=${animalId}`;
                        const respuesta = await fetch(url, { method: 'POST' });
                        // Si todo sale bien se informa, en caso contrario se indica que hubo un error en el servidor
                        if (respuesta.ok) {
                            console.log("¡Añadido!");
                        } else {
                            const errorData = await respuesta.json();
                            console.error("Error del servidor:", errorData);
                        }
                    } catch (error) {
                        console.error("Error de red:", error);
                        checkFavorito.checked = false;
                    }
                }
            });
        }

    } catch (error) {
        console.error("Error:", error);
    }
});