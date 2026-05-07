document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem('userId');
    const form = document.querySelector('.loginForm');

    if (!userId) {
        window.location.href = "../Index.html";
        return;
    }

    // --- PASO 1: CARGAR LOS DATOS ACTUALES ---
    try {
        const respuesta = await fetch(`http://127.0.0.1:8000/usuario/${userId}`);
        if (respuesta.ok) {
            const datos = await respuesta.json();
            
            // Rellenamos los inputs con los datos de la base de datos
            document.getElementById('regNombre').value = datos.NombreUsuario;
            document.getElementById('regFecha').value = datos.FechaNacimiento;
            document.getElementById('regCiudad').value = datos.Ciudad;
            document.getElementById('regEmail').value = datos.Email;
            // La contraseña la dejamos vacía por seguridad para que la escriba de nuevo
            
            // Cambiamos el texto del botón de "Crear cuenta" a "Guardar cambios"
            document.getElementById('createAccountButton').textContent = "Guardar cambios";
        }
    } catch (error) {
        console.error("Error al obtener datos:", error);
    }

    // --- PASO 2: ENVIAR LOS CAMBIOS (PUT) ---
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const datosActualizados = {
            NombreUsuario: document.getElementById('regNombre').value,
            FechaNacimiento: document.getElementById('regFecha').value,
            Ciudad: document.getElementById('regCiudad').value,
            Email: document.getElementById('regEmail').value,
            Password: document.getElementById('regPassword').value
        };

        if (!datosActualizados.Password) {
            alert("Por favor, confirma tu contraseña para guardar los cambios.");
            return;
        }

        try {
            const respuesta = await fetch(`http://127.0.0.1:8000/usuario/actualizar/${userId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosActualizados)
            });

            if (respuesta.ok) {
                alert("¡Información actualizada con éxito!");
                window.location.href = "Perfil.html";
            } else {
                const error = await respuesta.json();
                alert("Error: " + (error.detail || "No se pudo actualizar"));
            }
        } catch (error) {
            console.error("Error en la petición:", error);
            alert("Error de conexión con el servidor.");
        }
    });
});