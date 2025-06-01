package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un género cinematográfico o televisivo.
 * Un género puede asociarse a múltiples elementos multimedia.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "genero")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "medias" })
public class Genero {

  /**
   * Identificador único del género.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_genero")
  private Long id;

  /**
   * Nombre del género.
   * Debe ser único en el sistema.
   */
  @NotBlank(message = "El nombre no puede estar vacío")
  @Column(name = "nombre", nullable = false, unique = true)
  private String nombre;

  /**
   * Colección de elementos multimedia que pertenecen a este género.
   */
  @ManyToMany(mappedBy = "generos")
  @Builder.Default
  private Set<Media> medias = new HashSet<>();

  /**
   * Asocia un elemento multimedia a este género.
   *
   * @param media El elemento multimedia a asociar
   */
  public void addMedia(Media media) {
    medias.add(media);
    media.getGeneros().add(this);
  }

  /**
   * Desasocia un elemento multimedia de este género.
   *
   * @param media El elemento multimedia a desasociar
   */
  public void removeMedia(Media media) {
    medias.remove(media);
    media.getGeneros().remove(this);
  }
}
