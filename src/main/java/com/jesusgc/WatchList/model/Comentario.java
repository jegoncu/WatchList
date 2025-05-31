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
 * Entidad que representa un comentario y puntuación de un usuario sobre un elemento multimedia.
 * Los comentarios incluyen texto y una puntuación del 1 al 5.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "comentario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "usuario", "media" })
public class Comentario {

    /**
     * Identificador único del comentario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long id;

    /**
     * Usuario que realizó el comentario.
     */
    @NotNull(message = "El usuario no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /**
     * Elemento multimedia comentado.
     */
    @NotNull(message = "El media no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_media", nullable = false)
    private Media media;

    /**
     * Fecha y hora de creación del comentario.
     * Se establece automáticamente al crear el comentario.
     */
    @Column(name = "fecha")
    private LocalDateTime fecha;

    /**
     * Contenido textual del comentario.
     */
    @NotBlank(message = "El texto no puede estar vacío")
    @Column(name = "texto", columnDefinition = "TEXT")
    private String texto;

    /**
     * Puntuación dada al elemento multimedia.
     * Escala de 1 a 5 estrellas.
     */
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    @Column(name = "puntuacion")
    private Integer puntuacion;

    /**
     * Establece la fecha actual antes de persistir el comentario.
     */
    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
