# WatchList
## Proyecto Integrado para Desarrollo de Aplicaciones Web
### *Jesús González Cuenca*

#### Descripción del proyecto:

WatchList es una aplicación Web que ofrece un catalogo de peliculas, series de televisión, y trabajadores relacionados con la industria (directores, actores, etc). 

De cara a los usuarios, estos podrán consultar el catalogo empleando su sistema de filtrado y ordenación según los "metadatos" relacionados con cada elemento (pelicula, serie o persona).

Por ejemplo, un usuario podría filtrar peliculas estrenadas dentre los años 2000 y 2010, de género drama, disponibles en HBO o Netflix y ordenar los resultados de mayor a menor puntuación.

Se trata básicamente de una interfaz gráfica para hacer consultas a la base de datos.

Está funcionalidad tendrá su propia vista dínamica que cargará los distintos formularios según el tipo de objeto que se esté tratando (película, serie o persona) así como una galería dónde se cargarán los resultados de la consulta. 

Se podran abrir los objetos de la galería para ver todos los datos relacionados con los mismos (los "metadatos").

Esta vista será pública y no requerirá de un registro y login por parte del usuario.

Los usuarios podrá registrarse e iniciar sesión, lo que permite:

1. Crear Listas personalizadas: Un usuario podrá crear, editar y eliminar sus propias listas en las que guardar conjuntos de peliculas y/o series con un orden específico. Las listas pueden ser públicas o privadas.

2. Comentar/puntuar peliculas o series. En la vista de detalle de una pelicula/serie habrá una sección de reviews, que incluyen un comentario y una puntuación sobre 5.

3. Solicitar la inclusión de nuevas peliculas, series o personas en la base de datos: Si un usuario no encuentra un elemento en la página, puede crear una petición que luego será revisada por un administrador.

4. Seguir listas de otros usuarios: para poder encontrarlas rápidamente desde su propio perfil.

La función de VER listas (públicas) también es pública y no requiere de estar logueado/registrado. Estas listas públicas podrán encontrarse en una vista especifica dónde se pueden encontrar todas las vistas públicas, o viendo el perfil de un usuario que tenga alguna lista pública:

Los usuarios contarán con una vista de perfil (pública o privada) en la que se verán sus listas
públicas, las listas que sigue y su historial de comentarios.

Las películas/series se introducirán manualmente mediante un formulario solo accesible para la
administración. Como objetivo secundario este formulario se podrá rellenar automáticamente
volcando los datos desde una API externa cómo TMBb o IMDb.

#### Tecnologías y Justificación:

● **Java con Spring Boot**: para la lógica del negocio y la gestión del servidor.

● **JPA**: para la manipulación de datos.

● **MariaDB**: para almacenar información sobre películas, series, usuarios, listas y
comentarios y artistas.

● **Thymeleaf**: como motor de plantillas que facilita la creación de vistas dinámicas.

● **HTMX**: para obtener contenido HTML dinámicamente sin necesidad de JavaScript complejo.

● **CSS con Bootstrap**: para el diseño y estilo de la interfaz de usuario.

● **Azure**: como plataforma de despliegue para garantizar disponibilidad y escalabilidad.

● **Docker (futuro)**: No es un requisito inmediato, pero podría usarse para facilitar la
migración y despliegue en distintos entornos.

#### Modelo Entidad-Relacion
dbdiagram.io:

```dbml
Table media {
  id_media int [pk, increment]
  titulo varchar
  sinopsis text
  anio_estreno int
  puntuacion float
}

Table pelicula {
  id_media int [pk]
  duracion_min int
}

Table serie {
  id_media int [pk] 
  anio_estreno int
  anio_fin int
  n_temporadas int
}

Table usuario {
  id_usuario int [pk, increment]
  email varchar
  nombre varchar
  contrasenia varchar
  es_publico boolean
  es_admin boolean
}

Table comentario {
  id_comentario int [pk, increment]
  id_usuario int [not null]
  id_media int [not null]
  fecha datetime
  texto text
  puntuacion int [note: "Valor entre 1 y 5"]
}

Table lista {
  id_lista int [pk, increment]
  id_usuario int [not null]
  titulo varchar
  es_publica boolean
}

Table lista_media {
  id_lista int [not null]
  id_media int [not null]

  Primary Key (id_lista, id_media)
}

Table persona {
  id_persona int [pk, increment]
  nombre varchar
  fecha_nac date
  biografia text
}

Table plataforma {
  id_plataforma int [pk, increment]
  nombre varchar
  logo varchar
}

Table genero {
  id_genero int [pk, increment]
  nombre varchar
}

Table media_persona {
  id_media int [not null]
  id_persona int [not null]

  Primary Key (id_media, id_persona)
}

Table media_plataforma {
  id_media int [not null]
  id_plataforma int [not null]

  Primary Key (id_media, id_plataforma)
}

Table media_genero {
  id_media int [not null]
  id_genero int [not null]

  Primary Key (id_media, id_genero)
}

Ref: lista.id_usuario > usuario.id_usuario
Ref: lista_media.id_lista > lista.id_lista
Ref: lista_media.id_media > media.id_media

Ref: comentario.id_usuario > usuario.id_usuario
Ref: comentario.id_media > media.id_media

Ref: pelicula.id_media > media.id_media
Ref: serie.id_media > media.id_media 

Ref: media_persona.id_media > media.id_media
Ref: media_persona.id_persona > persona.id_persona

Ref: media_plataforma.id_media > media.id_media
Ref: media_plataforma.id_plataforma > plataforma.id_plataforma

Ref: media_genero.id_media > media.id_media
Ref: media_genero.id_genero > genero.id_genero

```
