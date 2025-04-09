package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 * Entidad que representa un comentario realizado por un usuario sobre un objeto media (película/serie).
 * Esta clase está mapeada a la tabla "comentario" en la base de datos.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity 
@Table(name = "comentario") 
@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder 
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"usuario", "media"})
public class Comentario {

    /**
     * Identificador único del comentario.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id_comentario") 
    private Long id;

    /**
     * Usuario que ha creado el comentario.
     * Representa el lado muchos en la relación muchos-a-uno con la entidad Usuario.
     */
    @NotNull(message = "El usuario no puede ser nulo") 
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "id_usuario", nullable = false) 
    private Usuario usuario;

    /**
     * Media (película o serie) sobre la que se ha realizado el comentario.
     * Representa el lado muchos en la relación muchos-a-uno con la entidad Media.
     */
    @NotNull(message = "El media no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "id_media", nullable = false) 
    private Media media;

    /**
     * Fecha y hora en la que se realizó el comentario.
     * Se establece automáticamente al crear el comentario si no se proporciona.
     */
    @Column(name = "fecha")
    private LocalDateTime fecha;

    /**
     * Texto del comentario escrito por el usuario.
     * No puede estar vacío.
     */
    @NotBlank(message = "El texto no puede estar vacío") 
    @Column(name = "texto", columnDefinition = "TEXT") 
    private String texto;

    /**
     * Puntuación que el usuario ha asignado a la media.
     * Debe estar entre 1 y 5.
     */
    @Min(value = 1, message = "La puntuación mínima es 1") 
    @Max(value = 5, message = "La puntuación máxima es 5") 
    @Column(name = "puntuacion")
    private Integer puntuacion;

    /**
     * Método que se ejecuta automáticamente antes de persistir un comentario.
     * Establece la fecha actual si no se ha proporcionado una.
     */
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}