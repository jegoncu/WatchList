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

/**
 * Clase abstracta que representa un elemento multimedia (película o serie)..
 *
 * La clase gestiona las relaciones con otras entidades del sistema como
 * comentarios,
 * listas de usuarios, créditos de profesionales, plataformas de streaming y
 * géneros.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "media")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(of = "id") // Solo usar ID para equals/hashCode para evitar problemas con lazy loading
@ToString(exclude = { "comentarios", "listas", "plataformas", "generos", "creditos" })

public abstract class Media {

  /**
   * Identificador único del elemento multimedia.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_media")
  private Long id;

  /**
   * Título del elemento multimedia.
   * Campo obligatorio que no puede estar vacío.
   */
  @NotBlank(message = "El título no puede estar vacío")
  @Column(name = "titulo", nullable = false)
  private String titulo;

  /**
   * Sinopsis o descripción del contenido multimedia.
   * Almacenado como TEXT para permitir descripciones extensas.
   * Este campo es opcional.
   */
  @Column(name = "sinopsis", columnDefinition = "TEXT")
  private String sinopsis;

  /**
   * Año de estreno del contenido multimedia.
   * Debe ser posterior a 1895.
   * Este campo es opcional.
   */
  @Min(value = 1895, message = "El año de estreno debe ser posterior a 1895")
  @Column(name = "anio_estreno")
  private Integer anioEstreno;

  /**
   * Puntuación del contenido multimedia.
   * Escala de 0 a 10.
   */
  @Max(value = 10, message = "La puntuación debe ser como máximo 10")
  @Min(value = 0, message = "La puntuación debe ser como mínimo 0")
  @Column(name = "puntuacion")
  private Float puntuacion;

  /**
   * URL de la imagen/póster representativo del contenido multimedia.
   * Campo opcional que almacena la ruta o URL hacia la imagen de portada.
   */
  @Column(name = "url_imagen")
  private String urlImagen;

  /**
   * Colección de comentarios realizados por usuarios sobre este contenido
   * multimedia.
   */
  @OneToMany(mappedBy = "media")
  @Builder.Default
  private Set<Comentario> comentarios = new HashSet<>();

  /**
   * Colección de listas de usuarios que incluyen este contenido multimedia.
   */
  @ManyToMany(mappedBy = "medias")
  @Builder.Default
  private Set<Lista> listas = new HashSet<>();

  /**
   * Colección de créditos que relacionan personas (actores, directores, etc.) con
   * este contenido.
   */
  @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<Credito> creditos = new HashSet<>();

  /**
   * Colección de plataformas de streaming donde está disponible este contenido.
   */
  @ManyToMany
  @JoinTable(name = "media_plataforma", joinColumns = @JoinColumn(name = "id_media"), inverseJoinColumns = @JoinColumn(name = "id_plataforma"))
  @Builder.Default
  private Set<Plataforma> plataformas = new HashSet<>();

  /**
   * Colección de géneros a los que pertenece este contenido multimedia.
   */
  @ManyToMany
  @JoinTable(name = "media_genero", joinColumns = @JoinColumn(name = "id_media"), inverseJoinColumns = @JoinColumn(name = "id_genero"))
  @Builder.Default
  private Set<Genero> generos = new HashSet<>();

  /**
   * Añade un crédito asociando una persona con un rol específico a este contenido
   * multimedia.
   *
   * @param persona La persona (actor, director, etc.) a asociar
   * @param rol     El rol que desempeña la persona en este contenido (ej:
   *                "Dirección", "Reparto", etc.)
   */
  public void addCredito(Persona persona, String rol) {
    Credito credito = new Credito(this, persona, rol);
    this.creditos.add(credito);
  }

  /**
   * Elimina un crédito específico de este contenido multimedia.
   *
   * @param credito El crédito a eliminar
   */
  public void removeCredito(Credito credito) {
    this.creditos.remove(credito);
    // Limpiar referencias bidireccionales para evitar problemas de consistencia
    credito.setMedia(null);
    credito.setPersona(null);
  }

  /**
   * Método abstracto que debe ser implementado por las subclases para identificar
   * el tipo de media.
   *
   * @return String que identifica el tipo de media ("PELICULA" o "SERIE")
   */
  public abstract String getMediaType();

}
