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
import lombok.Builder;

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

@ToString(exclude = { "comentarios", "listas", "plataformas", "generos", "creditos" })
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

    @Max(value = 10, message = "La puntuación debe ser como máximo 10")
    @Min(value = 0, message = "La puntuación debe ser como mínimo 0")
    @Column(name = "puntuacion")
    private Float puntuacion;

    @Column(name = "url_imagen")
    private String urlImagen;

    @Column(name = "url_trailer")
    private String urlTrailer;

    @OneToMany(mappedBy = "media")
    @Builder.Default
    private Set<Comentario> comentarios = new HashSet<>();

    @ManyToMany(mappedBy = "medias")
    @Builder.Default
    private Set<Lista> listas = new HashSet<>();

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Credito> creditos = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "media_plataforma", joinColumns = @JoinColumn(name = "id_media"), inverseJoinColumns = @JoinColumn(name = "id_plataforma"))
    @Builder.Default
    private Set<Plataforma> plataformas = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "media_genero", joinColumns = @JoinColumn(name = "id_media"), inverseJoinColumns = @JoinColumn(name = "id_genero"))
    @Builder.Default
    private Set<Genero> generos = new HashSet<>();

    public void addCredito(Persona persona, String rol) {
        Credito credito = new Credito(this, persona, rol);
        this.creditos.add(credito);
    }

    public void removeCredito(Credito credito) {
        this.creditos.remove(credito);
        credito.setMedia(null);
        credito.setPersona(null);
    }
}
