USE animal;

-- MAMÍFEROS
INSERT INTO animal (NombreAnimal, NombreCientifico, EsperanzaVida, FechaCreacion, Ubicacion, Reproduccion, Descripcion, Extinto, TipoAlimentacion, Especie, Actividad) VALUES
('León Africano', 'Panthera leo', '10-14 años', NOW(), 'Sabana Africana', 'Vivíparos', 'Conocido como el rey de la selva, destaca por su gran melena y vida en manada.', 0, 'Carnívoro', 'Mamífero', 'Nocturna'),
('Elefante Africano', 'Loxodonta africana', '60-70 años', NOW(), 'África Subsahariana', 'Vivíparos', 'Es el animal terrestre más grande del mundo, posee una trompa versátil y grandes colmillos.', 0, 'Herbívoro', 'Mamífero', 'Diurna'),
('Ornitorrinco', 'Ornithorhynchus anatinus', '12 años', NOW(), 'Australia', 'Ovíparos', 'Mamífero extraño que pone huevos, tiene pico de pato y cola de castor.', 0, 'Carnívoro', 'Mamífero', 'Nocturna');

-- AVES
INSERT INTO animal (NombreAnimal, NombreCientifico, EsperanzaVida, FechaCreacion, Ubicacion, Reproduccion, Descripcion, Extinto, TipoAlimentacion, Especie, Actividad) VALUES
('Águila Real', 'Aquila chrysaetos', '30 años', NOW(), 'Hemisferio Norte', 'Ovíparos', 'Una de las aves de presa más conocidas, con gran envergadura y vista aguda.', 0, 'Carnívoro', 'Ave', 'Diurna'),
('Pingüino Emperador', 'Aptenodytes forsteri', '20 años', NOW(), 'Antártida', 'Ovíparos', 'Ave no voladora adaptada al frío extremo, famosa por sus largas marchas sobre el hielo.', 0, 'Piscívoro', 'Ave', 'Diurna'),
('Búho Real', 'Bubo bubo', '20-25 años', NOW(), 'Europa y Asia', 'Ovíparos', 'Gran depredador aéreo nocturno con plumas que permiten un vuelo silencioso.', 0, 'Carnívoro', 'Ave', 'Nocturna');

-- REPTILES (Ahora como 'Reptil')
INSERT INTO animal (NombreAnimal, NombreCientifico, EsperanzaVida, FechaCreacion, Ubicacion, Reproduccion, Descripcion, Extinto, TipoAlimentacion, Especie, Actividad) VALUES
('Dragón de Komodo', 'Varanus komodoensis', '30 años', NOW(), 'Indonesia', 'Ovíparos', 'El lagarto más grande del mundo, posee una mordedura venenosa y gran olfato.', 0, 'Carnívoro', 'Reptil', 'Diurna'),
('Tortuga Galápagos', 'Chelonoidis nigra', '150 años', NOW(), 'Islas Galápagos', 'Ovíparos', 'Gigante herbívoro famoso por su longevidad y caparazón robusto.', 0, 'Herbívoro', 'Reptil', 'Diurna'),
('Cobra Real', 'Ophiophagus hannah', '20 años', NOW(), 'Sudeste Asiático', 'Ovíparos', 'La serpiente venenosa más larga del mundo, capaz de erguirse para intimidar.', 0, 'Carnívoro', 'Reptil', 'Diurna');

-- ANFIBIOS
INSERT INTO animal (NombreAnimal, NombreCientifico, EsperanzaVida, FechaCreacion, Ubicacion, Reproduccion, Descripcion, Extinto, TipoAlimentacion, Especie, Actividad) VALUES
('Axolote', 'Ambystoma mexicanum', '10-15 años', NOW(), 'México', 'Ovíparos', 'Anfibio con capacidad de regenerar extremidades que mantiene rasgos de larva en su vida adulta.', 0, 'Carnívoro', 'Anfibio', 'Nocturna'),
('Rana Flecha Azul', 'Dendrobates azureus', '5-7 años', NOW(), 'Surinam', 'Ovíparos', 'Pequeña rana de colores brillantes cuya piel secreta potentes toxinas.', 0, 'Insectívoro', 'Anfibio', 'Diurna'),
('Salamandra Común', 'Salamandra salamandra', '20 años', NOW(), 'Europa', 'Ovovivíparos', 'Anfibio negro con manchas amarillas que habita en bosques húmedos.', 0, 'Insectívoro', 'Anfibio', 'Nocturna');

-- PECES (Ahora como 'Pez')
INSERT INTO animal (NombreAnimal, NombreCientifico, EsperanzaVida, FechaCreacion, Ubicacion, Reproduccion, Descripcion, Extinto, TipoAlimentacion, Especie, Actividad) VALUES
('Tiburón Blanco', 'Carcharodon carcharias', '70 años', NOW(), 'Océanos templados', 'Ovovivíparos', 'El depredador marino más icónico, posee varias filas de dientes serrados.', 0, 'Carnívoro', 'Pez', 'Diurna'),
('Pez Payaso', 'Amphiprion ocellaris', '6-10 años', NOW(), 'Arrecifes de coral', 'Ovíparos', 'Pequeño pez de colores vivos que vive en simbiosis con las anémonas.', 0, 'Omnívoro', 'Pez', 'Diurna'),
('Atún Rojo', 'Thunnus thynnus', '15-30 años', NOW(), 'Océano Atlántico', 'Ovíparos', 'Pez migratorio de gran velocidad y tamaño, muy valorado en gastronomía.', 0, 'Carnívoro', 'Pez', 'Diurna');

-- INSECTOS
INSERT INTO animal (NombreAnimal, NombreCientifico, EsperanzaVida, FechaCreacion, Ubicacion, Reproduccion, Descripcion, Extinto, TipoAlimentacion, Especie, Actividad) VALUES
('Mariposa Monarca', 'Danaus plexippus', '6-9 meses', NOW(), 'América del Norte', 'Ovíparos', 'Famosa por su migración masiva de miles de kilómetros cada año.', 0, 'Herbívoro', 'Insecto', 'Diurna'),
('Escarabajo Hércules', 'Dynastes hercules', '1-2 años', NOW(), 'Sudamérica', 'Ovíparos', 'Uno de los insectos más fuertes, con un cuerno característico en los machos.', 0, 'Herbívoro', 'Insecto', 'Nocturna'),
('Mantis Religiosa', 'Mantis religiosa', '1 año', NOW(), 'Zonas templadas', 'Ovíparos', 'Insecto depredador conocido por su postura de oración y rapidez de ataque.', 0, 'Insectívoro', 'Insecto', 'Diurna');

-- ARÁCNIDOS (Ahora como 'Aracnido')
INSERT INTO animal (NombreAnimal, NombreCientifico, EsperanzaVida, FechaCreacion, Ubicacion, Reproduccion, Descripcion, Extinto, TipoAlimentacion, Especie, Actividad) VALUES
('Viuda Negra', 'Latrodectus mactans', '1-3 años', NOW(), 'América', 'Ovíparos', 'Araña pequeña conocida por la mancha roja en su abdomen y su veneno neurotóxico.', 0, 'Insectívoro', 'Aracnido', 'Nocturna'),
('Escorpión Emperador', 'Pandinus imperator', '6-8 años', NOW(), 'África Occidental', 'Vivíparos', 'Uno de los escorpiones más grandes, brilla bajo luz ultravioleta.', 0, 'Insectívoro', 'Aracnido', 'Nocturna'),
('Tarántula Goliat', 'Theraphosa blondi', '15-25 años', NOW(), 'Selva Amazónica', 'Ovíparos', 'La araña más grande del mundo por masa, capaz de cazar pequeños pájaros.', 0, 'Insectívoro', 'Aracnido', 'Nocturna');