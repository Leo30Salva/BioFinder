document.addEventListener("DOMContentLoaded", async () => {
    // Obtenemos el ID que guardamos en el Login
    const userId = window.localStorage.getItem('userId');

    // Por si no existe el ID
    if (!userId || userId === "null") {
        console.warn("No hay ID de usuario, redirigiendo...");
        window.location.href = "../Index.html"; 
        return; 
    }
    
    try {
        const respuesta = await fetch(`http://127.0.0.1:8000/usuario/${userId}`);
        const datos = await respuesta.json();

        // Muestro todos los datos del perfil
        if (respuesta.ok) {
            document.getElementById('perfilNombre').textContent = datos.NombreUsuario;
            document.getElementById('perfilFecha').textContent = datos.FechaNacimiento;
            document.getElementById('perfilCP').textContent = datos.Ciudad;
            document.getElementById('perfilEmail').textContent = datos.Email;
        }
    } catch (error) {
        console.error("Error al cargar el perfil:", error);
    }
});

document.getElementById('btnCambiarInfo').addEventListener('click', () => {
    // Avisamos que estamos en "modo edición"
    localStorage.setItem('editMode', 'true');
    // Redirigimos a la página donde está el formulario de registro
    window.location.href = "Registro.html"; 
});

document.getElementById('btnCambiarInfo').addEventListener('click', () => {
    // Solo nos aseguramos de que el userId existe antes de irnos
    if (localStorage.getItem('userId')) {
        window.location.href = "Modificar.html"; 
    }
});