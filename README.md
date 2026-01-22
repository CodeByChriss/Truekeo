# Truekeo - Proyecto integrador del grado de DAM

Somos una plataforma de intercambio local que facilita el trueque de objetos entre vecinos. Con un mapa interactivo en tiempo real, los usuarios publican anuncios, descubren intercambios disponibles en su área y coordinan encuentros para realizar trueques de manera segura.

Impulsamos una economía circular basada en el intercambio responsable, donde cada objeto encuentra un nuevo propietario en lugar de convertirse en residuo.

## Equipo de trabajo
[SM] **Scrum Master** [malmorox](https://github.com/malmorox)<br>
[CM] **Cloud Master**  [CodeByChriss](https://github.com/CodeByChriss)<br>
[DS] **Designer** [aiitttor](https://github.com/aiitttor)

## Tecnologías utilizadas
![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=Jetpack%20Compose&logoColor=white)
![Mapbox](https://img.shields.io/badge/Mapbox-007afc.svg?style=for-the-badge&logo=Mapbox&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black)

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

- [ ] **Optimización del diseño**
    - Adaptación a distintas densidades de pantalla y orientaciones.
    - Layouts responsivos.

- [ ] **Internacionalización y temas**
    - Soporte multiidioma.
    - Modo claro/oscuro (Day/Night).

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

### 🔥 Firebase Suite (BOM v34.7.0)

### 🖼️ Coil (v3.3.0)

**Funcionalidades implementadas:**
- Carga asíncrona de imágenes de productos y avatares de usuario con `AsyncImage`
- Recorte de imágenes con formas personalizadas (circular para avatares, redondeada para productos)
- Ajuste automático del contenido con `ContentScale.Crop`
