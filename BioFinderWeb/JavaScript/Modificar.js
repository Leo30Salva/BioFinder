document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem('userId');
    const form = document.querySelector('.loginForm');

    // Si no se encuentra el ID del usuario redirige hacia la página de inicio de sesión
    if (!userId) {
        window.location.href = "../Index.html";
        return;
    }

    try {
        // Llamo a la función de la API para cargar la información del usuario
        const respuesta = await fetch(`http://127.0.0.1:8000/usuario/${userId}`);
        if (respuesta.ok) {
            const datos = await respuesta.json();
            
            // Los datos por defecto tendrán el valor que ya tenían anteriormente, por si el usuario no quiere
            // cambiar sus valores
            document.getElementById('regNombre').value = datos.NombreUsuario;
            document.getElementById('regFecha').value = datos.FechaNacimiento;
            document.getElementById('regCiudad').value = datos.Ciudad;
            document.getElementById('regEmail').value = datos.Email;
            // La contraseña la dejamos vacía por seguridad para que la escriba de nuevo
            
            document.getElementById('createAccountButton').textContent = "Guardar cambios";
        }
    } catch (error) {
        console.error("Error al obtener datos:", error);
    }

    // 
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        // Los datos nuevos que el usuario ha ingresado
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
            // Llamo a la función de la API para actualizar la información
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