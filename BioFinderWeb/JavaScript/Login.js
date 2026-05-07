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

                if (respuesta.ok) {
                    alert("¡Bienvenido de nuevo, " + resultado.nombre + "!");
                    // Almaceno el ID para mostrar luego los datos en perfil
                    localStorage.setItem('userId', resultado.id);
                    localStorage.setItem('userName', resultado.nombre);
                    window.location.href = "Documentos/Home.html"; 
                } else {
                    alert("Error: " + resultado.detail);
                }
            } catch (error) {
                console.error("Error de conexión:", error);
                alert("No se pudo conectar con el servidor.");
            }
        });
    }
});