-- Crear base de datos
CREATE DATABASE animal;

-- Tablas
CREATE TABLE usuario (
	IdUser INT auto_increment PRIMARY KEY,
    NombreUsuario VARCHAR(20) NOT NULL,
    FechaCreacion datetime NOT NULL,
    FechaNacimiento DATE NOT NULL,
    CodigoPostal INT NOT NULL,
    Email VARCHAR(30) NOT NULL,
    Password VARCHAR(30) NOT NULL,
);

CREATE TABLE animal ( 
	IdAnimal INT auto_increment PRIMARY KEY,
    NombreAnimal VARCHAR(30) NOT NULL,
    NombreCientifico VARCHAR(50) NOT NULL,
    FechaCreacion datetime NOT NULL,
    Ubicacion VARCHAR(40) NOT NULL,
    Reproducción VARCHAR(40) NOT NULL,
    Descripción VARCHAR(200) NOT NULL,
    Extinto BOOL NOT NULL,
    TipoAlimentación VARCHAR(30) NOT NULL,
	Especie VARCHAR(30) NOT NULL,
    CONSTRAINT Alimentaciones CHECK (TipoAlimentación IN ('Herbívoro','Carnívoro','Omnívoro','Frugívoro','Insectívoro','Carroñero','Piscívoro','Detritívoros'),
    CONSTRAINT Especies CHECK (Especie IN ('Mamiferos','Aves','Reptiles','Anfibios','Insectos',' Aracnidos','Peces','Crustáceos','Moluscos','Equinodermos','Cnidarios','Anélidos')
);

Create TABLE Favoritos (
	idFavoritos INT auto_increment PRIMARY KEY,
    FOREIGN KEY (IdUser) REFERENCES usuario(IdUser) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (IdAnimal) REFERENCES animal(IdAnimal) ON DELETE CASCADE ON UPDATE CASCADE
);

Create TABLE Historial (
	idHistorial INT auto_increment PRIMARY KEY,
	FOREIGN KEY (IdUser) REFERENCES usuario(IdUser) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (IdAnimal) REFERENCES usuario(IdAnimal) ON DELETE CASCADE ON UPDATE CASCADE
)


