package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad abstracta que representa un contenido multimedia (película o serie).
 * Esta clase está mapeada a la tabla "media" en la base de datos y sirve como
 * clase base para los tipos específicos de contenido.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity
@Table(name = "media")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"comentarios", "listas", "personas", "plataformas", "generos"})
public abstract class Media {
    
    /**
     * Identificador único de la media.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_media")
    private Long id;
    
    /**
     * Título de la media.
     * No puede estar vacío.
     */
    @NotBlank(message = "El título no puede estar vacío")
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    /**
     * Sinopsis o resumen del contenido de la media.
     */
    @Column(name = "sinopsis", columnDefinition = "TEXT")
    private String sinopsis;
    
    /**
     * Año en que se estrenó la media.
     * Debe ser posterior al año 1895 (origen del cine).
     */
    @Min(value = 1895, message = "El año de estreno debe ser posterior a 1895")
    @Column(name = "anio_estreno")
    private Integer anioEstreno;
    
    /**
     * Puntuación media de la media.
     * Valor entre 0 y 10.
     */
    @Min(value = 0, message = "La puntuación mínima es 0")
    @Max(value = 10, message = "La puntuación máxima es 10")
    @Column(name = "puntuacion")
    private Float puntuacion;
    
    /**
     * Conjunto de comentarios realizados sobre esta media.
     * Representa el lado uno en la relación uno-a-muchos con la entidad Comentario.
     */
    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> comentarios = new HashSet<>();
    
    /**
     * Conjunto de listas que incluyen esta media.
     * Representa el lado muchos en la relación muchos-a-muchos con la entidad Lista.
     */
    @ManyToMany(mappedBy = "medias")
    private Set<Lista> listas = new HashSet<>();
    
    /**
     * Conjunto de personas relacionadas con esta media (actores, directores, etc.).
     * Representa la relación muchos-a-muchos con la entidad Persona.
     */
    @ManyToMany
    @JoinTable(
        name = "media_persona",
        joinColumns = @JoinColumn(name = "id_media"),
        inverseJoinColumns = @JoinColumn(name = "id_persona")
    )
    private Set<Persona> personas = new HashSet<>();
    
    /**
     * Conjunto de plataformas donde está disponible esta media.
     * Representa la relación muchos-a-muchos con la entidad Plataforma.
     */
    @ManyToMany
    @JoinTable(
        name = "media_plataforma",
        joinColumns = @JoinColumn(name = "id_media"),
        inverseJoinColumns = @JoinColumn(name = "id_plataforma")
    )
    private Set<Plataforma> plataformas = new HashSet<>();
    
    /**
     * Conjunto de géneros a los que pertenece esta media.
     * Representa la relación muchos-a-muchos con la entidad Genero.
     */
    @ManyToMany
    @JoinTable(
        name = "media_genero",
        joinColumns = @JoinColumn(name = "id_media"),
        inverseJoinColumns = @JoinColumn(name = "id_genero")
    )
    private Set<Genero> generos = new HashSet<>();
    
    /**
     * Añade un comentario a esta media y mantiene la consistencia de la relación bidireccional.
     * 
     * @param comentario El comentario que se añadirá a esta media
     */
    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setMedia(this);
    }
    
    /**
     * Elimina un comentario de esta media y mantiene la consistencia de la relación bidireccional.
     * 
     * @param comentario El comentario que se eliminará de esta media
     */
    public void removeComentario(Comentario comentario) {
        comentarios.remove(comentario);
        comentario.setMedia(null);
    }
    
    /**
     * Añade una persona a esta media y mantiene la consistencia de la relación bidireccional.
     * 
     * @param persona La persona que se añadirá a esta media
     */
    public void addPersona(Persona persona) {
        personas.add(persona);
        persona.getMedias().add(this);
    }
    
    /**
     * Elimina una persona de esta media y mantiene la consistencia de la relación bidireccional.
     * 
     * @param persona La persona que se eliminará de esta media
     */
    public void removePersona(Persona persona) {
        personas.remove(persona);
        persona.getMedias().remove(this);
    }
    
    /**
     * Añade un género a esta media y mantiene la consistencia de la relación bidireccional.
     * 
     * @param genero El género que se añadirá a esta media
     */
    public void addGenero(Genero genero) {
        generos.add(genero);
        genero.getMedias().add(this);
    }
    
    /**
     * Elimina un género de esta media y mantiene la consistencia de la relación bidireccional.
     * 
     * @param genero El género que se eliminará de esta media
     */
    public void removeGenero(Genero genero) {
        generos.remove(genero);
        genero.getMedias().remove(this);
    }
    
    /**
     * Añade una plataforma a esta media y mantiene la consistencia de la relación bidireccional.
     * 
     * @param plataforma La plataforma que se añadirá a esta media
     */
    public void addPlataforma(Plataforma plataforma) {
        plataformas.add(plataforma);
        plataforma.getMedias().add(this);
    }
    
    /**
     * Elimina una plataforma de esta media y mantiene la consistencia de la relación bidireccional.
     * 
     * @param plataforma La plataforma que se eliminará de esta media
     */
    public void removePlataforma(Plataforma plataforma) {
        plataformas.remove(plataforma);
        plataforma.getMedias().remove(this);
    }
}
