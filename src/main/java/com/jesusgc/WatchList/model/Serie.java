package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "serie")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Serie extends Media {

    @Column(name = "anio_fin")
    private Integer anioFin;

    @Min(value = 1, message = "El número de temporadas debe ser al menos 1")
    @Column(name = "n_temporadas")
    private Integer nTemporadas;

    public boolean haTerminado() {
        return anioFin != null;
    }

    public boolean estaEnEmision() {
        return anioFin == null;
    }

    @Override
    public String getMediaType() {
        return "serie";
    }
}