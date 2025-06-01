package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa una serie de televisión.
 * Extiende la clase Media añadiendo propiedades específicas como año de
 * finalización
 * y número de temporadas.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "serie")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Serie extends Media {

  /**
   * Año en que finalizó la serie.
   * Null si la serie sigue en emisión.
   */
  @Column(name = "anio_fin")
  private Integer anioFin;

  /**
   * Número total de temporadas de la serie.
   * Debe ser al menos 1.
   */
  @Min(value = 1, message = "El número de temporadas debe ser al menos 1")
  @Column(name = "n_temporadas")
  private Integer nTemporadas;

  /**
   * Verifica si la serie ha terminado.
   *
   * @return true si la serie tiene año de finalización
   */
  public boolean haTerminado() {
    return anioFin != null;
  }

  /**
   * Verifica si la serie está actualmente en emisión.
   *
   * @return true si la serie no tiene año de finalización
   */
  public boolean estaEnEmision() {
    return anioFin == null;
  }

  /**
   * Identifica el tipo de media como serie.
   *
   * @return "serie"
   */
  @Override
  public String getMediaType() {
    return "serie";
  }
}
