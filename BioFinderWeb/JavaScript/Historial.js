document.addEventListener("DOMContentLoaded", async () => {
    const contenedor = document.querySelector('.historialGallery');
    const userId = localStorage.getItem('userId');

    // Si no se encuentra el usuario se le indica que debe iniciar sesión para que se pueda mostrar su historial
    if (!userId) {
        contenedor.innerHTML = "<p>Debes iniciar sesión para ver tu historial.</p>";
        return;
    }
    // Si el usuario fue encontrado entonces se conectará a la API para utilizar la función que recoge el historial del usuario
    try {
        const respuesta = await fetch(`http://127.0.0.1:8000/historial/${userId}`);
        const animales = await respuesta.json();

        contenedor.innerHTML = ""; 

        // Si la longitud de animales es 0 significa que el usuario no ha consultado aún animales, por lo que se informa
        if (animales.length === 0) {
            contenedor.innerHTML = "<p>Aún no has visitado ningún animal.</p>";
            return;
        }

        // Para cada animal se crea una card para mostrarlo en pantalla
        animales.forEach(animal => {
            const card = document.createElement('div');
            card.className = 'historialPhotoCard';
            // Al hacer clic en la foto te llevará a su descripción
            card.innerHTML = `
                <a href="AnimalDescription.html?id=${animal.IdAnimal}">
                    <img src="${animal.ImagenURL}" alt="${animal.NombreAnimal}" onerror="this.src='../Multimedia/logo.png'">
                </a>
            `;
            contenedor.appendChild(card);
        });

    } catch (error) {
        console.error("Error al cargar historial:", error);
        contenedor.innerHTML = "<p>Error al conectar con el servidor.</p>";
    }
});