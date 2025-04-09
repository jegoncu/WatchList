package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa una película.
 * Esta clase extiende de Media y está mapeada a la tabla "pelicula" en la base de datos.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity
@Table(name = "pelicula")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Pelicula extends Media {
    
    /**
     * Duración de la película en minutos.
     * Debe ser un valor positivo mayor que cero.
     */
    @Min(value = 1, message = "La duración debe ser mayor a 0 minutos")
    @Column(name = "duracion_min")
    private Integer duracionMin;
}