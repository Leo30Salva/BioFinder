// Este código se ejecutará en todas las pantallas y como el propio nombre indica
// será para cosas más globales, que son el hecho de si el usuario quiere que se muestre
// la pagina de favoritos y el historial, por ello todas las pantallas deben tener este script
function aplicarPreferenciasMenu() {
    const mostrarFavs = localStorage.getItem('mostrarFavoritos');
    const mostrarHist = localStorage.getItem('mostrarHistorial');

    const itemFavs = document.getElementById('navFavorites');
    const itemHist = document.getElementById('navHistorial');

    // Si está guardado como false se ocultará
    if (itemFavs && mostrarFavs === 'false') {
        itemFavs.style.display = 'none';
    }

    if (itemHist && mostrarHist === 'false') {
        itemHist.style.display = 'none';
    }
}

document.addEventListener("DOMContentLoaded", aplicarPreferenciasMenu);