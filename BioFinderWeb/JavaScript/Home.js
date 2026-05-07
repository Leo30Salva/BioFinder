document.addEventListener("DOMContentLoaded", async () => {
    const preferencia = localStorage.getItem('preferenciaEspecie');
    const contenedor = document.getElementById('contenedorAnimales');

    if (preferencia) {
        try {
            const respuesta = await fetch(`http://127.0.0.1:8000/animales/filtrar?especie=${preferencia}`);
            const animales = await respuesta.json();

            // Limpiamos por si acaso
            contenedor.innerHTML = "";

            if (animales.length === 0) {
                contenedor.innerHTML = `<p class="noAnimals">No se encontraron animales de tipo ${preferencia}.</p>`;
                return;
            }

            // Recorremos los animales de la base de datos
            animales.forEach(animal => {
                // Creamos la estructura de la card
                const card = document.createElement('div');
                card.className = 'animalCard';

                // Usamos innerHTML para replicar tus clases de CSS
                card.innerHTML = `
                    <span class="animalCardName">${animal.NombreAnimal}</span>
                    <div class="animalCardImgBox greenBG">
                        <img src="../Multimedia/Animales/${animal.NombreAnimal.toLowerCase()}.png" alt="${animal.NombreAnimal}">
                    </div>
                    <a href="descripcion.html?id=${animal.IdAnimal}" class="moreInfoLink">Más información</a>
                `;

                contenedor.appendChild(card);
            });

        } catch (error) {
            console.error("Error cargando animales:", error);
            contenedor.innerHTML = "<p>Hubo un error al conectar con el servidor.</p>";
        }
    }
});