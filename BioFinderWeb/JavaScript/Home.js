document.addEventListener("DOMContentLoaded", async () => {
    const panel = document.getElementById('filterPanel');
    const openBtn = document.getElementById('openFilters');
    const closeBtn = document.getElementById('closeFilters');
    const applyBtn = document.getElementById('applyFilters');
    const contenedor = document.getElementById('contenedorAnimales');
    
    const inputBusqueda = document.querySelector('.searchInput');

    // Función para cargar los animales en el home
    async function cargarAnimales(filtros = null) {
        const contenedor = document.getElementById('contenedorAnimales');
        const tituloHome = document.querySelector('.recommendationTitle');
        
        contenedor.innerHTML = "<p>Cargando animales...</p>";
        
        // Me conecto a la función creada en la API para el filtrado de animales 
        try {
            let url = "http://127.0.0.1:8000/animales/filtrar_avanzado";
            let queryParts = [];

            // Almaceno aquellas recomendaciones para el usuario 
            const mostrarRecomendados = localStorage.getItem('mostrarRecomendados') !== 'false';

            // Si no hay filtros se cargarán los recomendados
            if (!filtros) {
                if (mostrarRecomendados) {
                    const pref = localStorage.getItem('preferenciaEspecie');
                    if (pref) {
                        let busqueda = pref.charAt(0).toUpperCase() + pref.slice(1);
                        if (busqueda.endsWith('s')) busqueda = busqueda.slice(0, -1);
                        if (busqueda === "Pece") busqueda = "Pez";
                        if (busqueda === "Aracnido") busqueda = "Arácnido";
                        
                        queryParts.push(`especies=${busqueda}`);
                        if (tituloHome) tituloHome.innerText = "Animales encontrados";
                    }
                } else {
                    if (tituloHome) tituloHome.innerText = "Todos los animales";
                }
            } else {
                // Si hay filtros activados compruebo de cuales se tratan, incluyendo si se esta buscando por nombre en el buscador
                // en el caso de que haya mas de una opción seleccionada las almaceno todas seguidas de una coma en filtros.
                if (filtros.especies?.length > 0) queryParts.push(`especies=${filtros.especies.join(",")}`);
                if (filtros.reproduccion?.length > 0) queryParts.push(`reproduccion=${filtros.reproduccion.join(",")}`);
                if (filtros.alimentacion?.length > 0) queryParts.push(`alimentacion=${filtros.alimentacion.join(",")}`);
                if (filtros.extinto !== undefined) queryParts.push(`extinto=${filtros.extinto}`);
                if (filtros.nombre) queryParts.push(`nombre=${encodeURIComponent(filtros.nombre)}`);
            }

            // Voy construyendo la URL con los filtros seleccionados, los filtros van al final de toda la URL
            const finalUrl = queryParts.length > 0 ? `${url}?${queryParts.join("&")}` : url;
            
            console.log("Llamando a la API:", finalUrl); 


            const respuesta = await fetch(finalUrl);
            if (!respuesta.ok) throw new Error("Error en la respuesta de la red");
            
            const animales = await respuesta.json();
            contenedor.innerHTML = "";

            if (!animales || animales.length === 0) {
                contenedor.innerHTML = "<p>No se encontraron animales.</p>";
                return;
            }

            // Hago un foreach para crear la card de cada animal y mostrarlo en pantalla
            animales.forEach(animal => {
                const card = document.createElement('div');
                card.className = 'animalCard';
                card.innerHTML = `
                    <span class="animalCardName">${animal.NombreAnimal}</span>
                    <div class="animalCardImgBox greenBG">
                        <img src="${animal.ImagenURL}" alt="${animal.NombreAnimal}" onerror="this.src='../Multimedia/logo.png'">
                    </div>
                    <a href="AnimalDescription.html?id=${animal.IdAnimal}" class="moreInfoLink">Más información</a>
                `;
                contenedor.appendChild(card);
            });

        } catch (error) {
            console.error("Error en cargarAnimales:", error);
            contenedor.innerHTML = "<p>Error de conexión con la API. Revisa que el servidor FastAPI esté corriendo.</p>";
        }
    }

    // Según el animal que se busque la función de cargarAnimal encontrará sus coincidencias
    if (inputBusqueda) {
        inputBusqueda.addEventListener('input', (e) => {
            const texto = e.target.value.trim();
            // Cargo los animales 
            cargarAnimales({
                nombre: texto,
                especies: [],
                reproduccion: [],
                alimentacion: [],
                extinto: true 
            });
        });
    }

    // Botones para el cierre o abrir del desplegable de preferencias
    if(openBtn) openBtn.onclick = () => panel.classList.add('active');
    if(closeBtn) closeBtn.onclick = () => panel.classList.remove('active');
    
    // Si se selecciona el boton de aplicar filtros se muestra
    if(applyBtn) {
        applyBtn.onclick = () => {
            const filtros = {
                especies: Array.from(panel.querySelectorAll('.prefGridSpecies input:checked'))
                               .map(i => i.parentElement.querySelector('span').innerText),
                reproduccion: Array.from(panel.querySelectorAll('.prefRowReproduction input:checked'))
                                   .map(i => i.value),
                alimentacion: Array.from(panel.querySelectorAll('.prefGridAlim input:checked'))
                                    .map(i => i.parentElement.textContent.trim()),
                extinto: document.getElementById('extinctSwitch').checked
            };
            
            cargarAnimales(filtros);
            panel.classList.remove('active');
        };
    }

    cargarAnimales(); 
});