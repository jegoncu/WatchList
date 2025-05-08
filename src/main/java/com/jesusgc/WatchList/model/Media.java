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
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_media")
    private Long id;
    
    @NotBlank(message = "El título no puede estar vacío")
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @Column(name = "sinopsis", columnDefinition = "TEXT")
    private String sinopsis;
    
    @Min(value = 1895, message = "El año de estreno debe ser posterior a 1895")
    @Column(name = "anio_estreno")
    private Integer anioEstreno;
    
    @Min(value = 0, message = "La puntuación mínima es 0")
    @Max(value = 10, message = "La puntuación máxima es 10")
    @Column(name = "puntuacion")
    private Float puntuacion;

    @Column(name = "url_trailer") 
    private String urlTrailer;

    @Column(name = "url_imagen")  
    private String urlImagen;
    
    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> comentarios = new HashSet<>();
    
    @ManyToMany(mappedBy = "medias")
    private Set<Lista> listas = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "media_persona",
        joinColumns = @JoinColumn(name = "id_media"),
        inverseJoinColumns = @JoinColumn(name = "id_persona")
    )
    private Set<Persona> personas = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "media_plataforma",
        joinColumns = @JoinColumn(name = "id_media"),
        inverseJoinColumns = @JoinColumn(name = "id_plataforma")
    )
    private Set<Plataforma> plataformas = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "media_genero",
        joinColumns = @JoinColumn(name = "id_media"),
        inverseJoinColumns = @JoinColumn(name = "id_genero")
    )
    private Set<Genero> generos = new HashSet<>();
    
    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setMedia(this);
    }
    
    public void removeComentario(Comentario comentario) {
        comentarios.remove(comentario);
        comentario.setMedia(null);
    }
    
    public void addPersona(Persona persona) {
        personas.add(persona);
        persona.getMedias().add(this);
    }
    
    public void removePersona(Persona persona) {
        personas.remove(persona);
        persona.getMedias().remove(this);
    }
    
    public void addGenero(Genero genero) {
        generos.add(genero);
        genero.getMedias().add(this);
    }
    
    public void removeGenero(Genero genero) {
        generos.remove(genero);
        genero.getMedias().remove(this);
    }
    
    public void addPlataforma(Plataforma plataforma) {
        plataformas.add(plataforma);
        plataforma.getMedias().add(this);
    }
    
    public void removePlataforma(Plataforma plataforma) {
        plataformas.remove(plataforma);
        plataforma.getMedias().remove(this);
    }
}
