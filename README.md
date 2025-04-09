# WatchList
## Proyecto Integrado para Desarrollo de Aplicaciones Web
### *Jesús González Cuenca*

### TO-DOs

-[ ] TODO escribir un README de verdad

-[x] TODO modelos

-[ ] TODO repositorios

-[ ] TODO servicios

-[ ] TODO controladores

---

### Posibles acciones para mejorar el modelo:

- [ ] **Seguridad en passwords**:
```java
// Para Usuario.java
@Column(name = "contrasenia", nullable = false)
private String contrasenia;
// Considerar añadir: @JsonIgnore para no serializar la contraseña
```
- [ ] **Control de cascada en relaciones**:
```java
// En relaciones *-a-muchos, específicar el fetchType
@ManyToMany(mappedBy = "generos", fetch = FetchType.LAZY)
```
- [ ] **Auditoría de entidades**:
```java
// Considerar añadir campos comunes de auditoría
@Column(name = "created_at")
private LocalDateTime createdAt;

@Column(name = "updated_at")
private LocalDateTime updatedAt;

@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    // etc
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```
- [ ] **Optimización para N+1 queries**:
```java
// Considerar usar @BatchSize en colecciones
@BatchSize(size = 20)
@OneToMany(mappedBy = "usuario")
private Set<Comentario> comentarios;
```
- [ ] **URL de imágenes**:
- Podría añadir un campo para URL de imágenes a Media (portadas/pósters)
- Podría añadir un campo para URL de avatar a Usuario (nprioridad muy baja)
#### Correcciones menores
- [ ] **Corregir inconsistencia en algunas relaciones ManyToMany**:
	Asegurar de que todos los HashSet estén inicializados con `@Builder.Default`
- [ ] **Observación sobre FetchType**:
	 Si bien LAZY es adecuado para relaciones OneToMany/ManyToMany, asegurarme de manejar correctamente la inicialización lazy para evitar LazyInitializationException.

### Diagrama Entidad-Relacion
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