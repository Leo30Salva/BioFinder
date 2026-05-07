// 1. Busco los botones
const buttonPreferences = document.getElementById('continueButtonPreferences');

// botón de Preferencias 
if (buttonPreferences) {
    buttonPreferences.addEventListener('click', function(event) {
        event.preventDefault(); 
        console.log("Preferencias aceptadas, redirigiendo a Home...");
        window.location.href = "../Home.html"; 
        // Nota: si ya estás en Preferencias.html, Home.html suele estar en la misma carpeta
    });
}
