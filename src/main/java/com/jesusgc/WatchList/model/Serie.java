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
 * Entidad que representa una serie de televisión.
 * Esta clase extiende de Media y está mapeada a la tabla "serie" en la base de datos.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity
@Table(name = "serie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Serie extends Media {
    
    /**
     * Año en que finalizó la emisión de la serie.
     * Si es null, indica que la serie sigue en emisión.
     */
    @Column(name = "anio_fin")
    private Integer anioFin;
    
    /**
     * Número de temporadas de la serie.
     * Debe ser al menos 1.
     */
    @Min(value = 1, message = "El número de temporadas debe ser al menos 1")
    @Column(name = "n_temporadas")
    private Integer nTemporadas;
    
    /**
     * Determina si una serie ha terminado su emisión.
     * 
     * @return true si la serie tiene un año de finalización, false en caso contrario
     */
    public boolean haTerminado() {
        return anioFin != null;
    }
    
    /**
     * Determina si una serie sigue en emisión.
     * 
     * @return true si la serie no tiene un año de finalización, false en caso contrario
     */
    public boolean estaEnEmision() {
        return anioFin == null;
    }
}