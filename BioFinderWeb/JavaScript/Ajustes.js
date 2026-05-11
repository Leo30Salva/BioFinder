document.addEventListener("DOMContentLoaded", () => {
    // Las variables que almacenan los valores de los botones
    const btnGuardar = document.querySelector('.buttonSettingsSave');
    const checkFavoritos = document.getElementById('checkFavs');
    const checkHistorial = document.getElementById('checkHistorial');
    const checkRecomendados = document.getElementById('checkRecomendados'); 
    const btnBorrar = document.querySelector('.buttonSettingsDelete');

    // Carga estado actual del switch
    checkFavoritos.checked = localStorage.getItem('mostrarFavoritos') !== 'false';
    checkHistorial.checked = localStorage.getItem('mostrarHistorial') !== 'false';
    checkRecomendados.checked = localStorage.getItem('mostrarRecomendados') !== 'false'; 

    // Guarda al hacer clic
    btnGuardar.addEventListener('click', () => {
        localStorage.setItem('mostrarFavoritos', checkFavoritos.checked.toString());
        localStorage.setItem('mostrarHistorial', checkHistorial.checked.toString());
        localStorage.setItem('mostrarRecomendados', checkRecomendados.checked.toString());
        
        alert("¡Configuración actualizada!");
        location.reload(); 
    });

    // Estructura para la opción de borrar
    if (btnBorrar) {
    btnBorrar.addEventListener('click', async () => {
        // Confirmación antes de realizar el borrado
        const confirmar = confirm("¿Estás seguro? Al confirmar borraras completamente tu cuenta.");
        
        // Una vez confirmada la acción se intenta coger el ID del usuario
        if (confirmar) {
            const userId = localStorage.getItem('userId'); 
            // Si no se encuentra el ID del usuario se informa error
            if (!userId) {
                alert("No se pudo encontrar la sesión del usuario.");
                return;
            }

            try {
                // Llamada a la API para utilizar el método de borrado de usuario mediante el ID
                const respuesta = await fetch(`http://127.0.0.1:8000/usuario/eliminar/${userId}`, {
                    method: 'DELETE'
                });

                // Se informa de que la cuenta fue eliminada correctamente
                if (respuesta.ok) {
                    alert("Tu cuenta ha sido eliminada. :(");
                    
                    // Limpio memoria y redirigo
                    localStorage.clear(); // Borro ID, preferencias, etc.
                    window.location.href = "../Index.html"; // Redirecciono a la página de login una vez la cuenta ha sido borrada
                } else {
                    const errorData = await respuesta.json();
                    alert("Error al borrar la cuenta: " + (errorData.detail || "Error desconocido"));
                }
            } catch (error) {
                console.error("Error:", error);
                alert("No se pudo conectar con el servidor para eliminar la cuenta.");
            }
        }
    });
}
});