document.addEventListener("DOMContentLoaded", async () => {


    const OPCIONES_ADMIN = {
        Especie: ['Mamífero','Ave','Reptil','Anfibio','Insecto','Aracnido','Pez','Crustáceo','Molusco','Equinodermo','Cnidario','Anélido'],
        TipoAlimentacion: ['Herbívoro','Carnívoro','Omnívoro','Frugívoro','Insectívoro','Carroñero','Piscívoro','Detritívoros'],
        Actividad: ['Diurna','Nocturna'],
        Reproduccion: ['Ovíparos','Ovovivíparos','Vivíparos']
    };

    const crearSelectorAdmin = (id, property, animal) => {
    const elemento = document.getElementById(id);

    const valorRealAPI = animal[property] !== undefined ? String(animal[property]).trim() : "";

    elemento.textContent = valorRealAPI;
    
    
    if (userRol !== 'admin' || !elemento) return;

    elemento.style.cursor = "pointer";
    elemento.style.borderBottom = "2px solid #6200ea";

    elemento.onclick = () => {
        if (elemento.querySelector('select')) return;

        const valorActual = elemento.textContent.trim();
        const select = document.createElement('select');
        select.className = "admin-select-field"; 

        OPCIONES_ADMIN[property].forEach(opt => {
            const o = document.createElement('option');
            o.value = opt;
            o.textContent = opt;
            
            if (opt === valorActual) o.selected = true;
            select.appendChild(o);
        });

        elemento.textContent = "";
        elemento.appendChild(select);

        select.onchange = () => {
            elemento.textContent = select.value;
            cambiosRealizados = true;
        };

        // Si el usuario hace clic fuera sin cambiar nada
        select.onblur = () => {
            if (elemento.contains(select)) {
                elemento.textContent = select.value;
            }
        };
        select.focus();
    };
    };

    // Variables para el cambio de info para los admin
    let cambiosRealizados = false;
    const userRol = localStorage.getItem('userRol');

    const params = new URLSearchParams(window.location.search);
    const animalId = params.get('id');
    const checkFavorito = document.getElementById('checkFavorito');

    const userId = localStorage.getItem('userId'); 
    
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
        const configurarEditable = (id, property) => {
            const elemento = document.getElementById(id);
            if (!elemento) return;
            if (userRol === 'admin') {
                elemento.contentEditable = "true";
                elemento.style.borderBottom = "1px dashed #6200ea"; 
                elemento.addEventListener('input', () => cambiosRealizados = true);
            }
            elemento.textContent = animal[property];
        };

        // El título
        const tituloNombre = document.querySelector(".detailsAnimalName");
        tituloNombre.textContent = animal.NombreAnimal;
        if(userRol === 'admin') {
            tituloNombre.contentEditable = "true";
            tituloNombre.addEventListener('input', () => cambiosRealizados = true);
        }

        // Los campos automáticos
        crearSelectorAdmin("valActividad", "Actividad", animal); 
        configurarEditable("valNombreCientifico", "NombreCientifico");
        crearSelectorAdmin("valEspecie", "Especie", animal);     
        configurarEditable("valVida", "EsperanzaVida");
        crearSelectorAdmin("valAlim", "TipoAlimentacion", animal);
        configurarEditable("descTexto", "Descripcion");
        crearSelectorAdmin("reproTexto", "Reproduccion", animal); 

        // El interruptor de Extinto
        const valExtinto = document.getElementById("valExtinto");
        valExtinto.textContent = animal.Extinto ? "Sí" : "No";
        if(userRol === 'admin') {
            valExtinto.style.cursor = "pointer";
            valExtinto.onclick = () => {
                valExtinto.textContent = valExtinto.textContent === "Sí" ? "No" : "Sí";
                cambiosRealizados = true;
            };
        }

        document.getElementById("imgAnimal").src = animal.ImagenURL;

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
                const userId = localStorage.getItem('userId'); 

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


    const backButton = document.querySelector(".detailsBackButton");
    if (backButton) {
        backButton.addEventListener('click', (e) => {
            if (cambiosRealizados && userRol === 'admin') {
                e.preventDefault(); 
                document.getElementById("modalGuardar").style.display = "flex";
            }
        });
    }

    const btnSi = document.getElementById("btnGuardarSi");
    const btnNo = document.getElementById("btnGuardarNo");

    if (btnNo) {
        btnNo.onclick = () => window.location.href = "Home.html";
    }

    if (btnSi) {
        btnSi.onclick = async () => {
            const bodyUpdate = {
                NombreAnimal: document.querySelector(".detailsAnimalName").textContent,
                Actividad: document.getElementById("valActividad").textContent,
                NombreCientifico: document.getElementById("valNombreCientifico").textContent,
                Especie: document.getElementById("valEspecie").textContent,
                EsperanzaVida: document.getElementById("valVida").textContent,
                TipoAlimentacion: document.getElementById("valAlim").textContent,
                Descripcion: document.getElementById("descTexto").textContent,
                Reproduccion: document.getElementById("reproTexto").textContent,
                Extinto: document.getElementById("valExtinto").textContent === "Sí"
            };

            const res = await fetch(`http://127.0.0.1:8000/animales/actualizar/${animalId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(bodyUpdate)
            });

            if (res.ok) {
                alert("Cambios guardados");
                window.location.href = "Home.html";
            }
        };
    }

});