let listaFavoritosGlobal = [];

document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem('userId'); 
    const grid = document.getElementById('favoritesGrid');
    const btnExportar = document.getElementById('btnExportarJSON');
    // Para empezar busco el ID del usuario, para poder cargar sus respectivos favoritos cargados
    if (!userId) {
        console.warn("No hay ID de usuario en localStorage");
        return;
    }

    // Intento conectarme a la API con su respectiva función para ir obteniendo sus favoritos
    // por ID del usuario
    try {
        const respuesta = await fetch(`http://127.0.0.1:8000/favoritos/usuario/${userId}`);
        const favoritos = await respuesta.json();
        listaFavoritosGlobal = favoritos;

        if (!grid) return;
        grid.innerHTML = ""; 

        // En el caso de que el usuario no tenga ningún animal guardado en favoritos lo indico
        if (favoritos.length === 0) {
            grid.innerHTML = "<p class='noFavsMessage'>Aún no tienes animales favoritos.</p>";
            return;
        }

        // Foreach para los favoritos, para cada animal que el usuario tenga en favoritos se creará una card
        favoritos.forEach(animal => {
            const card = document.createElement('div');
            card.className = 'favCard';
            // Cargo la card, para borrarlo si el usuario desea de esta página aado un botón arriba de la card con un corazón dentro
            // para que al pulsarlo llame a otra función y ese animal sea borrado de la página de favoritos del usuario
            card.innerHTML = `
                <span class="favCardName">${animal.NombreAnimal}</span>
                <div class="favImgBox greenBG">
                    <img src="${animal.ImagenURL}" alt="${animal.NombreAnimal}" onerror="this.src='../Multimedia/logo.png'">
                    <button class="heartButton active" onclick="removeFavorite(this, ${animal.IdAnimal})">❤️</button>
                </div>
                <a href="AnimalDescription.html?id=${animal.IdAnimal}" class="favInfoLink">Más información</a>
            `;
            grid.appendChild(card);
        });
    } catch (error) {
        console.error("Error cargando favoritos:", error);
    }
});

// Función para borrar los favoritos del usuario
async function removeFavorite(button, animalId) {
    const userId = localStorage.getItem('userId'); 
    const card = button.closest('.favCard');

    if (!userId) return;
    // Me conecto a la API y utilizo la función creada para eliminar animales por ID del animal y por ID del usuario
    try {
        const url = `http://127.0.0.1:8000/favoritos/eliminar?user_id=${userId}&animal_id=${animalId}`;
        const respuesta = await fetch(url, {
            method: 'DELETE'
        });

        if (respuesta.ok) {
            // AÑADE ESTA LÍNEA:
            listaFavoritosGlobal = listaFavoritosGlobal.filter(a => a.IdAnimal !== animalId);
            // Si todo funciona correctamente el animal será borrado de la tabla de favoritos del usuario
            // mostrando una animación en pantalla del animal siendo borrado
            card.style.transition = 'all 0.3s ease';
            card.style.opacity = '0';
            card.style.transform = 'scale(0.8)';
            
            setTimeout(() => {
                card.remove();
                // Si el grid no tiene elementos significa que el usuario no tiene animales en favoritos
                // poor lo que se le indica en la pantalla
                const grid = document.getElementById('favoritesGrid');
                if (grid && grid.children.length === 0) {
                    grid.innerHTML = "<p class='noFavsMessage'>Aún no tienes animales favoritos.</p>";
                }
            }, 300);
        } else {
            console.error("Error al eliminar del servidor");
        }
    } catch (error) {
        console.error("Error de conexión:", error);
    }
}

// Lógica para exportar el JSON 
document.getElementById('btnExportarJSON').addEventListener('click', () => {
    if (listaFavoritosGlobal.length === 0) {
        alert("No hay animales para exportar.");
        return;
    }

    // Crea el contenido del archivo
    const dataStr = JSON.stringify(listaFavoritosGlobal, null, 4);
    const blob = new Blob([dataStr], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    
    // Crea un enlace invisible y lo pulsa para descargar
    const link = document.createElement('a');
    link.href = url;
    link.download = "BioFinderFavorites.json";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
});