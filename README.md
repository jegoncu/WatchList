# WatchList

*Proyecto Integrado para Desarrollo de Aplicaciones Web*
**Jesús González Cuenca**

## Descripción del proyecto:

WatchList es una aplicación Web que ofrece un catálogo de películas, series de televisión, y profesionales relacionados con la industria (directores, actores, etc.).

Los usuarios pueden consultar el catálogo empleando el sistema de filtrado y ordenación según los metadatos relacionados con cada elemento (película, serie o persona). Por ejemplo, es posible filtrar películas estrenadas entre los años 2000 y 2010, de género drama, disponibles en HBO o Netflix y ordenar los resultados de mayor a menor puntuación.

La funcionalidad de navegación cuenta con vistas dinámicas que cargan formularios de filtrado según el tipo de contenido, junto con galerías donde se muestran los resultados. Cada elemento puede abrirse para ver todos sus datos relacionados y metadatos. Esta funcionalidad es pública y no requiere registro ni autenticación.

## Funcionalidades para usuarios registrados:

Los usuarios pueden registrarse e iniciar sesión, lo que habilita:

1. **Crear Listas personalizadas:** Los usuarios pueden crear, editar y eliminar sus propias listas para guardar conjuntos de películas y/o series con un orden específico. Las listas pueden configurarse como públicas o privadas.

2. **Comentar y puntuar películas o series:** En la vista de detalle de cada película/serie hay una sección de comentarios donde los usuarios pueden dejar reseñas que incluyen texto y una puntuación del 1 al 5.

3. **Gestionar perfil personal:** Los usuarios pueden editar su información personal, cambiar contraseñas y configurar la visibilidad de su perfil (público o privado).

## Funcionalidades públicas:

- **Visualización de listas públicas:** Cualquier usuario puede ver las listas marcadas como públicas en una vista específica dedicada.
- **Perfiles de usuario públicos:** Los perfiles configurados como públicos muestran las listas públicas del usuario.
- **Búsqueda global:** Sistema de búsqueda que abarca películas, series, personas y listas públicas.

## Panel de administración:

La aplicación cuenta con un panel de administración completo y protegido que permite:

- **Gestión completa de películas:** Crear, editar, eliminar y listar todas las películas del sistema.
- **Gestión completa de series:** Crear, editar, eliminar y listar todas las series del sistema.
- **Gestión de personas:** Administrar actores, directores y otros profesionales de la industria.
- **Gestión de usuarios:** Administrar cuentas de usuario y permisos de administrador.
- **Gestión de listas:** Supervisar y administrar todas las listas del sistema.

El acceso al panel está restringido únicamente a usuarios con permisos de administrador.

## Características técnicas implementadas:

- **Autenticación y autorización:** Sistema completo de registro, login y control de acceso.
- **Filtrado dinámico:** Sistema avanzado de filtros por género, año, plataforma, puntuación y más.
- **Ordenación configurable:** Múltiples criterios de ordenación en todas las listas.
- **Comentarios y puntuaciones:** Sistema completo de reseñas de usuarios.
- **Gestión de listas:** Creación, edición y gestión de listas personalizadas.
- **Búsqueda global:** Búsqueda unificada en todo el contenido del sitio.
- **Panel administrativo:** Interface completa para gestión de contenido.
- **Actualizaciones dinámicas:** Uso de HTMX para experiencia de usuario fluida sin recargas de página al filtrar y/o ordenar.

