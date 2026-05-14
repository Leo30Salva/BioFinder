document.addEventListener("DOMContentLoaded", async () => {
    const userId = window.localStorage.getItem('userId');

    // Si no se encuentra el ID del usuario se  redirecciona a la página de login
    if (!userId) {
        console.warn("No hay ID de usuario, redirigiendo...");
        window.location.href = "../Index.html"; 
        return; 
    }
    
    try {

        const respuesta = await fetch(`http://127.0.0.1:8000/usuario/${userId}`);

        // Si se encuentra el usuario se cargan los datos del usuario en el perfil
        if (respuesta.ok) {
            const datos = await respuesta.json();
            document.getElementById('perfilNombre').textContent = datos.NombreUsuario;
            document.getElementById('perfilFecha').textContent = datos.FechaNacimiento;
            document.getElementById('perfilCP').textContent = datos.Ciudad;
            document.getElementById('perfilEmail').textContent = datos.Email;

            // Cargo la imagen del perfil
            const imgAvatar = document.getElementById("perfilAvatarImg");
            if (datos.ImagenPerfil && datos.ImagenPerfil !== "null" && datos.ImagenPerfil !== "") {
                imgAvatar.src = datos.ImagenPerfil;
            } else {
                imgAvatar.src = "../Multimedia/susana.png";
            }
        } else {
            console.error("Error en la respuesta de la API");
        }

    } catch (error) {
        console.error("Error al cargar el perfil:", error);
    }
});

// Botón para cambiar la información del usuario
const btnCambiarInfo = document.getElementById('btnCambiarInfo');
if (btnCambiarInfo) {
    btnCambiarInfo.addEventListener('click', () => {
        localStorage.setItem('editMode', 'true');
        window.location.href = "Modificar.html"; 
    });
}
