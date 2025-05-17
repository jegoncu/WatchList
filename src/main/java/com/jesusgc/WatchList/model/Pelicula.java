package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pelicula")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Pelicula extends Media {

    @Min(value = 1, message = "La duración debe ser mayor a 0 minutos")
    @Column(name = "duracion_min")
    private Integer duracionMin;
    
    @Column(name = "url_trailer")
    private String urlTrailer;
    
    @Override
    public String getTipo() {
        return "pelicula";
    }
}