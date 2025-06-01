package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa una lista personalizada de elementos multimedia.
 * Los usuarios pueden crear listas públicas o privadas con películas y series.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "lista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "usuario", "medias" })
public class Lista {

  /**
   * Identificador único de la lista.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_lista")
  private Long id;

  /**
   * Usuario propietario de la lista.
   */
  @NotNull(message = "El usuario no puede ser nulo")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_usuario", nullable = false)
  private Usuario usuario;

  /**
   * Título de la lista.
   */
  @NotBlank(message = "El título no puede estar vacío")
  @Column(name = "titulo", nullable = false)
  private String titulo;

  /**
   * Indica si la lista es pública o privada.
   * Por defecto es true (pública).
   */
  @Column(name = "es_publica")
  private Boolean esPublica;

  /**
   * Colección de elementos multimedia incluidos en la lista.
   */
  @ManyToMany
  @Builder.Default
  @JoinTable(name = "lista_media", joinColumns = @JoinColumn(name = "id_lista"), inverseJoinColumns = @JoinColumn(name = "id_media"))
  private Set<Media> medias = new HashSet<>();

  /**
   * Añade un elemento multimedia a la lista.
   *
   * @param media El elemento multimedia a añadir
   */
  public void addMedia(Media media) {
    medias.add(media);
    media.getListas().add(this);
  }

  /**
   * Elimina un elemento multimedia de la lista.
   *
   * @param media El elemento multimedia a remover
   */
  public void removeMedia(Media media) {
    medias.remove(media);
    media.getListas().remove(this);
  }

  /**
   * Establece la lista como pública por defecto antes de persistir.
   */
  @PrePersist
  protected void onCreate() {
    if (esPublica == null) {
      esPublica = true;
    }
  }
}
