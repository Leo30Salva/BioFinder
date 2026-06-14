#  BioFinder Android Compose - Documentación de testing  

En este documento redactaré la documentación técnica de mi app **BioFinder**, explicaré varios detalles como pueda ser decisiones que he tomado y la organización de la interfaz de usuario.

---

## 1. Arquitectura del Software

Para este proyecto he implementado la arquitectura ** (Model-View-ViewModel)** recomendada por Google.

### Estructura de Carpetas

El código fuente de la aplicación se organiza dentro del módulo principal `app` en la ruta de paquetes `java/com/example/biofinderandroid/`. La estructura actual está compuesta por los siguientes elementos reales:

* **`Documentos/`:** Es la carpeta que contiene las pantallas completas de la interfaz de usuario. Actualmente alberga:
    * `Register.kt`: Pantalla de registro del usuario.
    * `Login.kt`: Pantalla de login del usuario.
    * `AnimalDescription.kt`: Contiene información de cada animal.
    * `Favoritos.kt`: Donde se almacenan los animales favoritos del usuario.
    * `Footer.kt`: Donde se almacenan el bottom bar de la app.
    * `Header.kt`: Donde se almacena el top bar de la aplicación.
    * `Historial.kt`: Donde se almacena el historial de navegación de cada usuario
    * `Home.kt`: Pantalla principal de la app, donde se pueden buscar animales.
    * `Modificar.kt`: Pantalla donde se puede modificar información de la cuenta de usuario.
    * `Perfil.kt`: Donde se muestra información acerca de la cuenta de usuario.
    * `Recomendaciones.kt`: Donde queda ha elección del usuario elegir su especie animal favorita.
    * `SelecciónImagen.kt`: Permite al usuario cambiar su foto de perfil.
     
* **`ui.theme/`:** Carpeta encargada de almacenar los archivos de configuración visual global de Jetpack Compose:
    * `Color.kt`: Definición de la paleta de colores de la app.
    * `Theme.kt`: Configuración del tema claro/oscuro del sistema.
    * `Type.kt`: Configuración de las fuentes y estilos tipográficos.
* **Raíz del Paquete (`com.example.biofinderandroid`):** Aquí se encuentran los archivos de inicialización de la aplicación.
* **Carpeta de AndroidTest (`androidTest/java/com/example/biofinderandroid/`):** Aloja el archivo de pruebas automatizadas de la interfaz:
    * `BioFinderRegisterUITest.kt`: El test que verifica nodos y la simulación del comportamiento del usuario en la pantalla de registro.
* **Carpeta de Test (`test/java/com/example/biofinderandroid/`):** Aloja el archivos de pruebas independientes automatizadas:
    * `RegisterValidatorTest.kt`: Verifica que la aplicación tenga un comportamiento adecuado tanto cuando los campos sean rellenados como cuando no sean rellenados.

---

## 2. Manual de Identidad Visual

Para que la aplicación mantenga una coherencia estética en todas sus pantallas, nos basamos en las directrices de **Material Design 3**.

### Paleta de Colores 
Definidos en el archivo `ui/theme/Color.kt`:

| Rol del Color | Código HEX | Uso en la Aplicación |
| :--- | :--- | :--- |
| **Primary** | `#2A9D8F` | Botones principales, acciones clave y marca. |
| **Secondary** | `#E76F51` | Elementos de alertas o estados activos. |
| **Background** | `#FAFAFA` | Fondo general de las pantallas. |
| **Surface** | `#FFFFFF` | Fondo de las cards, campos de texto e inputs. |

### Tipografía
* **`headlineLarge` (Títulos Principales):**
    * **Uso:** Título superior de las pantallas principales.
    * **Estilo:** Montserrat Bold.
    * **Tamaño:** `32.sp` (Interlineado: `40.sp`).
* **`titleLarge` (Títulos Secundarios / Subtítulos):**
    * **Uso:** Encabezados de secciones o títulos de tarjetas importantes.
    * **Estilo:** Montserrat ExtraBold.
    * **Tamaño:** `22.sp` (Interlineado: `28.sp`).
* **`bodyMedium` (Textos de Cuerpo e Inputs):**
    * **Uso:** Texto general y el contenido que escribe el usuario dentro de los `TextField`.
    * **Estilo:** Montserrat Regular.
    * **Tamaño:** `16.sp` (Interlineado: `24.sp`).
* **`labelLarge` (Textos de Botones Principales):**
    * **Uso:** Texto dentro de los botones de acción destacados.
    * **Estilo:** Montserrat Bold (Negrita).
    * **Tamaño:** `18.sp` (Interlineado: `24.sp`).
* **`labelMedium` (Botones Secundarios / Enlaces):**
    * **Uso:** Acciones secundarias o textos clicables pequeños.
    * **Estilo:** Montserrat Medium.
    * **Tamaño:** `14.sp` (Interlineado: `20.sp`).
* **`bodySmall` (Textos Informativos / Errores):**
    * **Uso:** Mensajes de validación o aclaraciones al pie de los componentes.
    * **Estilo:** Montserrat Medium.
    * **Tamaño:** `12.sp` (Interlineado: `16.sp`).

---

## 3. Estructura de Componentes de la Interfaz (UI)

Toda la interfaz de usuario de **BioFinder** está construida utilizando Jetpack Compose.

### Flujo de Datos y Gestión de Estados (State)

Los componentes visuales son funciones puras que reaccionan a estados mutables (`State`), su función es basicamente comunicarse con la app en todo momento para que sepa siempre que el valor de una variable haya tenido un cambio.

---

### Jerarquía y Anatomía Estándar de una Pantalla

Para mantener la consistencia en toda la aplicación (ya sea el Login, el Registro o las pantallas de búsqueda), se sigue una estructura jerárquica de componentes organizada en tres niveles esenciales:

#### 1. Contenedor Estructural 
* **`Scaffold`**: Es el esqueleto de la pantalla. Se utiliza de forma generalizada porque gestiona automáticamente los espacios del sistema, el color de fondo general de la identidad visual y permite integrar de forma limpia barras superiores (`TopAppBar`) o barras de navegación inferior.

#### 2. Organizadores de Diseño 
* **`Column` / `Row`**: Se utilizan para estructurar los elementos visuales en el espacio. Utilizo `Column` combinado con modificadores de scroll (`verticalScroll`) para garantizar que la interfaz sea accesible en pantallas de cualquier tamaño.
* **`Box`**: Lo utilizo para superponer elementos (como poner un indicador de carga circular encima de todo el contenido) o alinear componentes de forma libre.

#### 3. Componentes de Interacción y Contenido (Nivel de Detalle)

Cada pantalla se compone con los siguientes bloques de construcción fundamentales:

* **`Text`**: Muestra las cadenas de caracteres aplicando los estilos de la fuente *Montserrat*.
* **`Image` / `Icon`**: Elementos gráficos, procesados de forma eficiente mediante la librería **Coil** cuando provienen de URLs externas.
* **`OutlinedTextField`**: Campos de entrada de datos con un borde definido. Con `onValueChange` captura la entrada del usuario en tiempo real.
* **`Button`**: Botones contenedores con uso del reglamento de Material Design 3 destinados a las acciones principales de la pantalla.
* **`TextButton`**: Textos con función de botones, para acciones secundarias o enlaces de navegación.