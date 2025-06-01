package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa una película.
 * Extiende la clase Media añadiendo propiedades específicas como duración y
 * trailer.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "pelicula")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Pelicula extends Media {

  /**
   * Duración de la película en minutos.
   * Debe ser mayor a 0 minutos.
   */
  @Min(value = 1, message = "La duración debe ser mayor a 0 minutos")
  @Column(name = "duracion_min")
  private Integer duracionMin;

  /**
   * URL del trailer de la película.
   * Campo opcional.
   */
  @Column(name = "url_trailer")
  private String urlTrailer;

  /**
   * Identifica el tipo de media como película.
   *
   * @return "pelicula"
   */
  @Override
  public String getMediaType() {
    return "pelicula";
  }
}
