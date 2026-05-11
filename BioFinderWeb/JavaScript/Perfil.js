document.addEventListener("DOMContentLoaded", async () => {
    const userId = window.localStorage.getItem('userId');

    // Si no se eencuentra el ID del usuario se  redirecciona a la página de login
    if (!userId) {
        console.warn("No hay ID de usuario, redirigiendo...");
        window.location.href = "../Index.html"; 
        return; 
    }
    
    try {

        const respuesta = await fetch(`http://127.0.0.1:8000/usuario/${userId}`);
        // SI se encuentra el usuario se cargan los datos del usuario en el perfil
        if (respuesta.ok) {
            const datos = await respuesta.json();
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
    localStorage.setItem('editMode', 'true');
    window.location.href = "Modificar.html"; 
});

