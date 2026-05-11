document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector('.preferencesForm');

    form.addEventListener('submit', (e) => {
        e.preventDefault();

        const seleccion = document.querySelector('input[name="especie"]:checked');

        // SI el botón de preferencia está siendo seleccionado se guarda en el localStorage
        if (seleccion) {
            localStorage.setItem('preferenciaEspecie', seleccion.value);
            window.location.href = "Home.html";
        } else {
            alert("Por favor, selecciona una especie.");
        }
    });
});