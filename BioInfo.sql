-- Crear base de datos
DROP DATABASE IF EXISTS animal;
CREATE DATABASE animal;
USE animal;

-- Tabla Usuario
CREATE TABLE usuario (
    IdUser INT AUTO_INCREMENT PRIMARY KEY,
    NombreUsuario VARCHAR(20) NOT NULL,
    FechaCreacion DATETIME NOT NULL,
    FechaNacimiento DATE NOT NULL,
    Ciudad VARCHAR(30) NOT NULL,
    Email VARCHAR(30) NOT NULL,
    Password VARCHAR(30) NOT NULL
);

-- Tabla Animal
CREATE TABLE animal ( 
    IdAnimal INT AUTO_INCREMENT PRIMARY KEY,
    NombreAnimal VARCHAR(30) NOT NULL,
    NombreCientifico VARCHAR(50) NOT NULL,
    EsperanzaVida VARCHAR(25) NOT NULL,
    FechaCreacion DATETIME NOT NULL,
    Ubicacion VARCHAR(1000) NOT NULL,
    Reproduccion VARCHAR(40) NOT NULL,
    Descripcion VARCHAR(500) NOT NULL,
    ImagenURL VARCHAR(500) NOT NULL,
    Extinto BOOL NOT NULL,
    TipoAlimentacion VARCHAR(30) NOT NULL,
    Especie VARCHAR(30) NOT NULL,
    Actividad VARCHAR(15) NOT NULL,
    -- Corregidos los paréntesis de cierre abajo
    CONSTRAINT Alimentaciones CHECK (TipoAlimentacion IN ('Herbívoro','Carnívoro','Omnívoro','Frugívoro','Insectívoro','Carroñero','Piscívoro','Detritívoros')),
    CONSTRAINT Especies CHECK (Especie IN ('Mamífero','Ave','Reptil','Anfibio','Insecto','Aracnido','Pez','Crustáceo','Molusco','Equinodermo','Cnidario','Anélido')),
    CONSTRAINT Actividades CHECK (Actividad IN ('Diurna','Nocturna'))
);

-- Tabla Favoritos
CREATE TABLE favoritos (
    IdFavorito INT AUTO_INCREMENT PRIMARY KEY,
    IdUser INT NOT NULL, -- Primero creamos la columna
    IdAnimal INT NOT NULL, -- Primero creamos la columna
    FOREIGN KEY (IdUser) REFERENCES usuario(IdUser) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (IdAnimal) REFERENCES animal(IdAnimal) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabla Historial
CREATE TABLE historial (
    idHistorial INT AUTO_INCREMENT PRIMARY KEY,
    IdUser INT NOT NULL,
    IdAnimal INT NOT NULL,
    CantidadConsultas INT DEFAULT 0,
    UltimaConsulta DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (IdUser) REFERENCES usuario(IdUser) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (IdAnimal) REFERENCES animal(IdAnimal) ON DELETE CASCADE ON UPDATE CASCADE
);