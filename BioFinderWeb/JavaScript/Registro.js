document.addEventListener("DOMContentLoaded", () => {

    const registroForm = document.querySelector('.loginForm'); 

    registroForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Bloqueo la recarga de página del formulario al pulsar el botón

        try {
            // Recojo los valores
            const nombre = document.getElementById('regNombre').value;
            const email = document.getElementById('regEmail').value;
            const fecha = document.getElementById('regFecha').value;
            const ciudad = document.getElementById('regCiudad').value;
            const pass = document.getElementById('regPassword').value;

            const datosUsuario = {
                NombreUsuario: nombre,
                Email: email,
                FechaNacimiento: fecha,
                Ciudad: ciudad, 
                Password: pass
            };

            // Guardo los valores utilizando el endpoint de registro
            const respuesta = await fetch('http://127.0.0.1:8000/registro', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosUsuario)
            });

            const resultado = await respuesta.json();

            // Si sale bien redirecciono a la página de login, en caso contrario indico que los datos son erroneos
            if (respuesta.ok) {
                alert("¡Registro exitoso! Bienvenido, " + resultado.NombreUsuario);
                window.location.href = "../Index.html"; 
            } else {
                console.warn("Respuesta API error:", resultado);
                alert("Error: " + (resultado.detail || "Datos incorrectos"));
            }

        } catch (error) {
            alert("No se pudo conectar con el servidor.");
        }
    });
});

