# Arquitectura de la Aplicación WatchList

## 1. Introducción

WatchList es una aplicación web diseñada para proporcionar a los usuarios un catálogo completo de películas, series de televisión y profesionales de la industria. Los usuarios pueden navegar, filtrar, ordenar contenido, crear listas de seguimiento personalizadas, comentar y calificar títulos. La aplicación también cuenta con un backend administrativo para la gestión de contenido.

Este documento describe la arquitectura de alto nivel de la aplicación WatchList, basándose en el modelo de entidades Java implementado.

## 2. Tecnologías Principales

La aplicación utiliza las siguientes tecnologías:

-   **Backend:** Java 17 con Spring Boot (versión 3.4.5).
    -   Spring MVC para la gestión de peticiones web.
    -   Spring Data JPA para la persistencia de datos.
    -   Spring Boot Actuator para monitorización y gestión.
    -   Spring Boot Starter Mail para funcionalidades de correo.
    -   Spring Boot Starter Validation para la validación de datos.
-   **Persistencia de Datos:**
    -   JPA (Java Persistence API) para el mapeo objeto-relacional.
    -   MariaDB como base de datos relacional (cliente `mariadb-java-client` 3.5.3).
-   **Motor de Plantillas Frontend:** Thymeleaf para la representación del lado del servidor de HTML dinámico.
-   **Interactividad Frontend:** HTMX para mejorar la experiencia del usuario con actualizaciones dinámicas de contenido sin recargas completas de página.
-   **Seguridad:**
    -   La autenticación de usuarios se gestiona en `UsuarioController`, estableciendo el estado de sesión, incluyendo un indicador de administrador. jBCrypt (versión 0.4) se utiliza para el hashing de contraseñas.
    -   La autorización para funcionalidades específicas, como el acceso al panel de administración, se infiere del estado de sesión del usuario y la lógica de redirección en los controladores.
-   **Estilos:** CSS personalizado, organizado modularmente. Se utilizan variables CSS para la tematización.
-   **Construcción y Dependencias:** Apache Maven.
-   **Utilidades de Desarrollo:**
    -   Lombok para reducir código boilerplate.
    -   Spring Boot DevTools para recarga en caliente y otras ayudas en desarrollo.
-   **Despliegue:** Azure. La aplicación se empaqueta como un archivo WAR, desplegada en un contenedor de servlets como Tomcat (incluido como dependencia `provided`).
-   **Contenerización (Futuro):** Docker.

## 3. Capas de la Aplicación

La aplicación sigue una arquitectura por capas:

### 3.1. Capa de Presentación (Frontend)

-   **Plantillas Thymeleaf:** Ubicadas en `src/main/resources/templates`. Definen la estructura y presentación de las páginas web.
    -   Un diseño principal (`_layout.html`) proporciona una estructura consistente.
    -   Los componentes de UI reutilizables se gestionan como fragmentos (p. ej., `_header.html`, `_sidebar.html`).
    -   Vistas específicas para funcionalidades como detalles de películas (`peliculas/pelicula-detalle.html`), series (`series/serie-detalle.html`), y administración.
-   **Activos Estáticos:** Ubicados en `src/main/resources/static`.
    -   **CSS:** Hojas de estilo en `static/css`, incluyendo `styles.css`, `variables.css`, `base.css`, `layout.css` y CSS específico para componentes.
    -   **JavaScript:** Bibliotecas del lado del cliente como HTMX (`js/htmx.min.js`) y scripts personalizados (ej. `admin-form-pelicula.js`, `dropdown.js`).
-   **HTMX:** Utilizado para obtener fragmentos HTML del servidor y actualizar partes de la página dinámicamente.

### 3.2. Capa de Aplicación (Backend)

-   **Spring Boot:** Proporciona el marco central. `WatchListApplication.java` es el punto de entrada. `ServletInitializer.java` para despliegue WAR.
-   **Controladores (`com.jesusgc.WatchList.controller`):** Gestionan las solicitudes HTTP, interactúan con los servicios y seleccionan vistas Thymeleaf.
    -   Ejemplos: `AdminController.java`, `PeliculasController.java`, `UsuarioController.java`, `GenteController.java`, `SeriesController.java`, `ListasController.java`.
-   **Servicios (`com.jesusgc.WatchList.service`):** Encapsulan la lógica de negocio.
    -   Ejemplos: `PeliculaService.java`, `UsuarioService.java`, `ComentarioService.java`, `GeneroService.java`, `PersonaService.java`, `SerieService.java`, `ListaService.java`, `PlataformaService.java`.
-   **Seguridad:** La lógica de autenticación y autorización se maneja directamente en el código de la aplicación. jBCrypt se utiliza para el almacenamiento seguro de las contraseñas de los usuarios (`Usuario`).

### 3.3. Capa de Datos

-   **Entidades JPA (`com.jesusgc.WatchList.model`):** Clases Java que representan las tablas de la base de datos.
    -   `Usuario`: Información de los usuarios (id, email, nombre, contraseña hasheada, esPublico, esAdmin). Relacionado con `Comentario` y `Lista`.
    -   `Media` (abstracta): Entidad base para contenido multimedia (id, titulo, sinopsis, anioEstreno, puntuacion, urlImagen), usando estrategia de herencia `JOINED`. Relacionada con `Comentario`, `Lista`, `Credito`, `Plataforma`, y `Genero`.
    -   `Pelicula` (extiende `Media`): Detalles específicos de películas (duracionMin, urlTrailer).
    -   `Serie` (extiende `Media`): Detalles específicos de series (anioFin, nTemporadas).
    -   `Genero`: Catálogo de géneros (id, nombre). Relacionado con `Media`.
    -   `Persona`: Información sobre profesionales de la industria (id, nombre, fechaNac, biografia, fotoUrl). Relacionado con `Credito`.
    -   `Plataforma`: Catálogo de plataformas de streaming (id, nombre, logo). Relacionado con `Media`.
    -   `Comentario`: Comentarios y puntuaciones de los usuarios (id, fecha, texto, puntuacion). Relacionado con `Usuario` y `Media`.
    -   `Credito`: Entidad de asociación que representa la participación de una `Persona` en una `Media` con un `rol` y `personaje`. Utiliza `CreditoId` como clave primaria compuesta.
    -   `CreditoId` (`@Embeddable`): Clave primaria compuesta para `Credito` (mediaId, personaId, rol).
    -   `Lista`: Listas de seguimiento creadas por los usuarios (id, titulo, esPublica). Relacionado con `Usuario` y `Media`.
-   **Relaciones y Tablas de Unión Definidas por las Entidades:**
    -   La tabla `media_genero` es gestionada por la relación `@ManyToMany` entre `Media` y `Genero` (definida en `Media`).
    -   La tabla `media_plataforma` es gestionada por la relación `@ManyToMany` entre `Media` y `Plataforma` (definida en `Media`).
    -   La tabla `lista_media` es gestionada por la relación `@ManyToMany` entre `Lista` y `Media` (definida en `Lista`), e incluye `orden_en_lista`.
    -   La tabla `credito` representa la relación muchos-a-muchos con atributos entre `Media` y `Persona`, gestionada por la entidad `Credito`.
-   **Repositorios (`com.jesusgc.WatchList.repository`):** Interfaces Spring Data JPA para el acceso y manipulación de datos, una por cada entidad principal.
    -   Ejemplos: `PeliculaRepository.java`, `UsuarioRepository.java`, `GeneroRepository.java`, `CreditoRepository.java`.
-   **MariaDB:** Base de datos relacional que almacena todos los datos de la aplicación, cuya estructura se corresponde con el modelo de entidades JPA.

## 4. Aspectos Destacados de la Estructura de Directorios

-   `src/main/java/com/jesusgc/WatchList/`: Código fuente Java.
    -   `controller/`: Controladores Spring MVC.
    -   `model/`: Entidades JPA (ej. `Pelicula.java`, `CreditoId.java`).
    -   `repository/`: Repositorios Spring Data JPA.
    -   `service/`: Servicios de lógica de negocio.
-   `src/main/resources/`: Recursos de la aplicación.
    -   `static/`: Activos estáticos (CSS, JavaScript, imágenes).
    -   `templates/`: Plantillas Thymeleaf.
    -   `application.properties`: Archivo principal de configuración de la aplicación (incluye datasource).
-   `pom.xml`: Archivo de configuración del proyecto Maven.
-   `README.md`: Documentación general del proyecto.

## 5. Flujo de Datos (Ejemplo General)

1.  El usuario interactúa con la interfaz en el navegador.
2.  Una solicitud HTTP (posiblemente vía HTMX) se envía al backend.
3.  Un controlador de Spring MVC en el paquete `controller` recibe la solicitud.
4.  El controlador invoca al servicio apropiado en el paquete `service`.
5.  El servicio utiliza los repositorios del paquete `repository` para interactuar con MariaDB a través de JPA y las entidades del paquete `model`.
6.  Los datos se devuelven al controlador.
7.  El controlador selecciona una vista o fragmento Thymeleaf.
8.  Thymeleaf procesa la plantilla con los datos.
9.  El HTML resultante se envía al navegador.

Este documento proporciona una instantánea de la arquitectura. Se actualizará a medida que el proyecto evolucione.


