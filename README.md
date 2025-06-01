# WatchList

*Proyecto Integrado para Desarrollo de Aplicaciones Web*
**Jesús González Cuenca**

## Descripción del proyecto:

WatchList es una aplicación Web que ofrece un catálogo de películas, series de televisión, y trabajadores relacionados con la industria (directores, actores, etc.).

De cara a los usuarios, estos podrán consultar el catálogo empleando su sistema de filtrado y ordenación según los "metadatos" relacionados con cada elemento (película, serie o persona). Por ejemplo, un usuario podría filtrar películas estrenadas entre los años 2000 y 2010, de género drama, disponibles en HBO o Netflix y ordenar los resultados de mayor a menor puntuación.

Esta funcionalidad tendrá su propia vista dinámica que cargará los distintos formularios según el tipo de objeto que se esté tratando (película, serie o persona) así como una galería dónde se cargarán los resultados de la consulta. Se podrán abrir los objetos de la galería para ver todos los datos relacionados con los mismos (los "metadatos"). Esta vista será pública y no requerirá de un registro y login por parte del usuario.

Para conocer los detalles técnicos y la arquitectura de la aplicación, consulta el documento [ARQUITECTURE.md](ARQUITECTURE.md).

## Funcionalidades para usuarios registrados:

Los usuarios podrán registrarse e iniciar sesión, lo que permite:

1.  **Crear Listas personalizadas:** Un usuario podrá crear, editar y eliminar sus propias listas en las que guardar conjuntos de películas y/o series con un orden específico. Las listas pueden ser públicas o privadas.
2.  **Comentar/puntuar películas o series:** En la vista de detalle de una película/serie habrá una sección de reviews, que incluyen un comentario y una puntuación sobre 5.
<!-- 3.  **Solicitar la inclusión de nuevo contenido:** Si un usuario no encuentra un elemento en la página, puede crear una petición que luego será revisada por un administrador para añadir nuevas películas, series o personas a la base de datos (futuro).
4.  **Seguir listas de otros usuarios:** Para poder encontrarlas rápidamente desde su propio perfil (futuro). -->

La función de **VER listas** (públicas) también es pública y no requiere de estar logueado/registrado. Estas listas públicas podrán encontrarse en una vista específica donde se muestran todas las listas públicas, o viendo el perfil de un usuario que tenga alguna lista pública.

Los usuarios contarán con una **vista de perfil** (configurable como pública o privada) en la que se verán sus listas públicas<!--, las listas que sigue y su historial de comentarios (futuro). -->

## Administración del contenido:

Las películas, series y personas se introducirán manualmente mediante un formulario solo accesible para la administración. <!-- Como objetivo secundario, este formulario se podrá rellenar automáticamente volcando los datos desde una API externa como TMDb o IMDb (futuro). -->

<!-- TODO: estilar: -->
<!-- -[] login -->
<!-- -[] registro -->
<!-- -[] crear-lista -->
<!-- -[] actualizar-perfil -->
