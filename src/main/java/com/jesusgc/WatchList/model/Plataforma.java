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
 * Entidad que representa una plataforma de streaming o distribución.
 * Una plataforma puede alojar múltiples elementos multimedia.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "plataforma")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "medias" })
public class Plataforma {

    /**
     * Identificador único de la plataforma.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plataforma")
    private Long id;

    /**
     * Nombre de la plataforma.
     * Debe ser único en el sistema.
     */
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    /**
     * URL del logo de la plataforma.
     * Campo opcional.
     */
    @Column(name = "logo")
    private String logo;

    /**
     * Colección de elementos multimedia disponibles en esta plataforma.
     */
    @ManyToMany(mappedBy = "plataformas")
    @Builder.Default
    private Set<Media> medias = new HashSet<>();

    /**
     * Añade un elemento multimedia a esta plataforma.
     *
     * @param media El elemento multimedia a añadir
     */
    public void addMedia(Media media) {
        medias.add(media);
        media.getPlataformas().add(this);
    }

    /**
     * Remueve un elemento multimedia de esta plataforma.
     *
     * @param media El elemento multimedia a remover
     */
    public void removeMedia(Media media) {
        medias.remove(media);
        media.getPlataformas().remove(this);
    }
}
