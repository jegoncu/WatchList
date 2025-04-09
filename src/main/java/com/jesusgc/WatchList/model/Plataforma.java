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
 * Entidad que representa una plataforma de streaming.
 * Esta clase está mapeada a la tabla "plataforma" en la base de datos.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity
@Table(name = "plataforma")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"medias"})
public class Plataforma {

    /**
     * Identificador único de la plataforma.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plataforma")
    private Long id;
    
    /**
     * Nombre de la plataforma.
     * Debe ser único y no puede estar vacío.
     */
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;
    
    /**
     * URL o ruta de la imagen del logotipo de la plataforma.
     */
    @Column(name = "logo")
    private String logo;
    
    /**
     * Conjunto de medias (películas/series) disponibles en esta plataforma.
     * Representa el lado inverso de la relación muchos-a-muchos con la entidad Media.
     */
    @ManyToMany(mappedBy = "plataformas")
    @Builder.Default
    private Set<Media> medias = new HashSet<>();
    
    /**
     * Añade una media a esta plataforma y mantiene la consistencia de la relación bidireccional.
     * 
     * @param media La entidad Media que se añadirá a esta plataforma
     */
    public void addMedia(Media media) {
        medias.add(media);
        media.getPlataformas().add(this);
    }
    
    /**
     * Elimina una media de esta plataforma y mantiene la consistencia de la relación bidireccional.
     * 
     * @param media La entidad Media que se eliminará de esta plataforma
     */
    public void removeMedia(Media media) {
        medias.remove(media);
        media.getPlataformas().remove(this);
    }
}