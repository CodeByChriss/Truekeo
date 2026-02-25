# Truekeo - Proyecto integrador del grado de DAM

Somos una plataforma de intercambio local que facilita el trueque de objetos entre vecinos. Con un mapa interactivo en tiempo real, los usuarios publican anuncios, descubren intercambios disponibles en su área y coordinan encuentros para realizar trueques de manera segura.

Impulsamos una economía circular basada en el intercambio responsable, donde cada objeto encuentra un nuevo propietario en lugar de convertirse en residuo.

App en: **[Google Play](https://play.google.com/store/apps/details?id=com.chaima.truekeo)**

## Equipo de trabajo
[SM] **Scrum Master** [malmorox](https://github.com/malmorox)<br>
[CM] **Cloud Master**  [CodeByChriss](https://github.com/CodeByChriss)<br>
[DS] **Designer** [aiitttor](https://github.com/aiitttor)

## Tecnologías utilizadas
![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=Jetpack%20Compose&logoColor=white)
![Mapbox](https://img.shields.io/badge/Mapbox-007afc.svg?style=for-the-badge&logo=Mapbox&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black)
![Supabase](https://img.shields.io/badge/Supabase-3FCF8E.svg?style=for-the-badge&logo=Supabase&logoColor=white)

## Prototipo (Figma)

El diseño inicial de la aplicación ha sido desarrollado en Figma, donde se ha definido la estructura visual, los flujos de navegación y las principales pantallas de la app.

[![Prototipo en figma](resources/figma-prototype.png)](https://www.figma.com/design/iiBDX15OGVsRuzPoiFn4LV/Truekeo)

🔗 [Enlace al prototipo en Figma](https://www.figma.com/design/iiBDX15OGVsRuzPoiFn4LV/Truekeo?node-id=0-1&t=ABp0jil8KM5YN9Hf-1)

## Metodología de trabajo

El desarrollo del proyecto se está llevando a cabo siguiendo la metodología ágil SCRUM, organizando el trabajo en sprints para facilitar la planificación, el seguimiento y la entrega incremental de funcionalidades.

### Sprint 1 – Análisis y prototipado (1 semana)

Durante el primer sprint se sentaron las bases del proyecto, centrando el trabajo en la definición visual y conceptual de la aplicación.

***Objetivos alcanzados:***

- [x] Definición de la idea y alcance del proyecto (con ayuda del profesor).
- [x] Creación del prototipo de la aplicación en Figma.
- [x] Diseño inicial de pantallas.

### Sprint 2 – Metodología de trabajo e inicio de interfaz (3 semanas)

En el segundo sprint se ha comenzado con el desarrollo técnico del proyecto y la implementación de la interfaz.

***Objetivos en alcanzados:***

- [x] Creación del proyecto en GitHub.
- [x] Implementación de las pantallas de:
  - Splash
  - Login
  - Registro
- [x] Diseño y uso de temas/estilos en Compose.

### Sprint 3 – Navegación, menús y adaptabilidad (3 semanas)

En el tercer sprint se amplía la funcionalidad de la aplicación desarrollando la navegación completa, optimizando la interfaz para diferentes dispositivos y añadiendo características de accesibilidad e internacionalización.

***Objetivos alcanzados:***

- [x] **Desarrollo de la interfaz principal**
  - Menús de navegación y fragments/tabs (no hacen falta más activities).
  - Sistema completo de navegabilidad entre pantallas.

- [x] **Optimización del diseño**
  - Adaptación a distintas densidades de pantalla y orientaciones.
  - Layouts responsivos.

- [x] **Internacionalización y temas**
  - Soporte multiidioma.
  - Modo claro/oscuro (Day/Night).

### Sprint 4 – Integración, pruebas y publicación (4 semanas)

En el cuarto sprint se ha llevado a cabo la integración completa del sistema, la resolución de incidencias detectadas y la preparación de la versión final para su distribución pública.

***Objetivos alcanzados:***

- [x] **Integración completa del proyecto**
  - Conexión definitiva entre frontend (Jetpack Compose), backend (Firebase) y almacenamiento (Supabase).
  - Integración del mapa interactivo con publicaciones en tiempo real.
  - Resolución de conflictos y errores derivados de la integración de módulos.

- [x] **Pruebas, validación y documentación**
  - Pruebas funcionales de navegación, autenticación y publicación de truekes.
  - Validación de flujos completos de trueke.
  - Corrección de bugs detectados durante el testing.
  - Elaboración de informes técnicos y documentación del proyecto.

- [x] **Publicación y distribución**
  - Generación de versión release firmada.
  - Configuración de ficha en Google Play.
  - Subida y despliegue de la aplicación en producción.

🚀 **Release alpha publicada en Google Play**

## Implementación técnica y uso de librerías

Este apartado documenta las principales librerías utilizadas en el proyecto y cómo se han implementado.

### 🗺️ Mapbox SDK (v11.17.1)

**Propósito:** Implementación de un mapa interactivo para visualizar ubicaciones de Trueke.

**Funcionalidades implementadas:**
- Mapa interactivo integrado con Jetpack Compose.
- Marcadores personalizados (`Marker`) con colores del tema de la aplicación.
    ```java
    import com.mapbox.maps.extension.compose.annotation.Marker
    ```
- Sistema de clicks en marcadores que despliega un Bottom Sheet con información detallada del Trueke.
- Animaciones suaves de cámara (`flyTo`) al seleccionar ubicaciones.

### 🔥 Firebase Authentication y Firestore

**Propósito:** Gestión centralizada de autenticación y persistencia de perfiles de usuario en Firestore

**Funcionalidades implementadas:**
- **Autenticación híbrida y social:** Integración de `FirebaseAuth` para registro con Email/Password y soporte para `GoogleAuthProvider`.
- **Identificación dual de usuario:** Sistema de inicio de sesión flexible que permite el acceso mediante ***correo electrónico*** o ***nombre de usuario***, realizando consultas dinámicas en Firestore.
- **Garantía de unicidad (transacciones):** Uso de `db.runTransaction` para asegurar que no existan duplicados en la colección de `usernames` durante el registro o actualización.
- **Gestión automática de perfiles:** Generación de nombres de usuario aleatorios con lógica de reintento automático para nuevos registros mediante proveedores externos (Google).
- **Flujos ssíncronos con corrutinas:** Implementación de `suspend functions` y extensión `.await()` para un manejo eficiente y no bloqueante de las tareas de Firebase.
- **Seguridad en el registro:** Implementación de envío automático de correo de verificación tras la creación de cuenta exitosa.

### 💾 Supabase

**Propósito:** Almacenamiento de archivos binarios y gestión de activos multimedia de alta disponibilidad mediante Supabase Storage.

**Funcionalidades implementadas:**
- **Gestión de buckets:** Configuración de contenedores públicos para el almacenamiento centralizado de avatares de usuario.
- **Optimización de almacenamiento (Upsert):** Implementación de lógica de subida con sobrescritura automática (`upsert = true`) para minimizar el uso de cuota en el tier gratuito.
- **Políticas de seguridad (RLS):** Configuración de Row Level Security para controlar los permisos de lectura y escritura de archivos desde el cliente móvil.
- **Generación de URLs públicas:** Obtención dinámica de enlaces permanentes para la persistencia de rutas de imagen en los perfiles de Firestore.

### 🖼️ Compressor (v3.0.1)

https://github.com/zetbaitsu/Compressor

**Propósito:** Optimización de recursos multimedia mediante la reducción del peso de las imágenes antes de la transferencia de datos.

**Funcionalidades implementadas:**
- **Compresión adaptativa:** Reducción de dimensiones a un máximo de 320px, garantizando nitidez en pantallas de alta densidad (hasta 160dp) sin penalizar el rendimiento.
- **Ahorro de ancho de banda:** Disminución drástica del peso del archivo (calidad 80%) para acelerar las subidas en conexiones móviles.
- **Integración con corrutinas:** Procesamiento asíncrono de imágenes para evitar bloqueos en el hilo principal de la interfaz durante la manipulación de archivos.

### 🖼️ Coil (v3.3.0)

**Funcionalidades implementadas:**
- Carga asíncrona de imágenes de productos y avatares de usuario con `AsyncImage`
- Recorte de imágenes con formas personalizadas (circular para avatares, redondeada para productos)
- Ajuste automático del contenido con `ContentScale.Crop`

### 🧩 Material Icons Extended

```gradle
dependencies {
    implementation("androidx.compose.material:material-icons-extended")
}
```

**Propósito:** Incorporación de un conjunto ampliado de iconos Material para mejorar la experiencia visual y la claridad de la interfaz de usuario.

### 🔄 Gestión de Estados del Trueke

La aplicación gestiona un ciclo de vida completo para cada intercambio:

1. **OPEN:** Visible en el mapa para todos los usuarios.
2. **RESERVED:** Acuerdo entre dos usuarios, el anuncio desaparece del mapa general.
3. **COMPLETED:** Intercambio finalizado, registro histórico para ambos perfiles.

### 🏷️ Sistema de Branding e Inteligencia de Datos

Para garantizar que la base de datos de objetos sea coherente y facilitar la búsqueda de productos, hemos implementado un sistema de autocompletado inteligente basado en un motor de búsqueda local.

**Objeto BrandData**
En lugar de depender de llamadas constantes a una API externa, hemos diseñado un objeto de utilidad (BrandData) que contiene un repositorio curado de más de 200 marcas líderes categorizadas por sectores (Tecnología, Moda, Hogar, Motor, etc.).

**Características principales:**
- Búsqueda Reactiva: A medida que el usuario escribe, el sistema filtra en tiempo real las coincidencias, permitiendo seleccionar marcas complejas con solo un par de pulsaciones.
- Normalización Automática: El sistema corrige automáticamente el formato de texto (ej. convierte "sAmSuNg" en "Samsung"), asegurando una estética uniforme en toda la plataforma.
- Sanitización de Seguridad: Se filtran caracteres especiales y emojis para evitar inconsistencias en el almacenamiento.

```kotlin
// Lógica de filtrado en tiempo real en BrandData.kt
fun search(query: String, limit: Int = 8): List<String> {
    if (query.isBlank()) return emptyList()

    return knownBrands
        .filter { it.contains(query, ignoreCase = true) } // Búsqueda case-insensitive
        .take(limit) // Limitamos resultados para optimizar la UI
}
```