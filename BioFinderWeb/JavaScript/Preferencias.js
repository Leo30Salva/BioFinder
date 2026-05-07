document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector('.preferencesForm');

    form.addEventListener('submit', (e) => {
        e.preventDefault();

        // 1. Buscamos cuál de los radio buttons está marcado
        const seleccion = document.querySelector('input[name="especie"]:checked');

        if (seleccion) {
            const especieFavorita = seleccion.value;
            
            // 2. Guardamos la preferencia en localStorage
            localStorage.setItem('preferenciaEspecie', especieFavorita);
            
            console.log("Preferencia guardada:", especieFavorita);

            // 3. Redirigimos al Home
            window.location.href = "./Home.html";
        } else {
            alert("Por favor, selecciona una especie antes de continuar.");
        }
    });
});