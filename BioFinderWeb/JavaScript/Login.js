document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.querySelector('.loginForm'); 

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const email = document.getElementById('logEmail').value;
            const password = document.getElementById('logPassword').value;

            // Compruebo que la cuenta exista
            try {
                const respuesta = await fetch('http://127.0.0.1:8000/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ Email: email, Password: password })
                });

                const resultado = await respuesta.json();

                // Si todo es correcto, y la contraseña coincide con el correo indicado entonces accederá a la sesión y entrará 
                // en la cuenta
                if (respuesta.ok) {
                    alert("¡Bienvenido de nuevo, " + resultado.nombre + "!");
                    
                    // Guardo userId en localStorage para usarlo en adelante para comprobaciones 
                    localStorage.setItem('userId', resultado.id); 
                    localStorage.setItem('userName', resultado.nombre);
                    
                    // Una vez se inicie sesión activo los recomendados por defecto nada más iniciar sesión
                    localStorage.setItem('mostrarRecomendados', 'true');
                    
                    // Hago la referencia a la página de preferencias
                    window.location.href = "Documentos/Preferencias.html"; 
                }
            } catch (error) {
                console.error("Error de conexión:", error);
                alert("No se pudo conectar con el servidor.");
            }
        });
    }
});