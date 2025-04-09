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
 * Entidad que representa un género cinematográfico o televisivo.
 * Esta clase está mapeada a la tabla "genero" en la base de datos.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity
@Table(name = "genero")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"medias"})
public class Genero {

    /**
     * Identificador único del género.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero")
    private Long id;
    
    /**
     * Nombre del género.
     * Debe ser único y no puede estar vacío.
     */
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;
    
    /**
     * Colección de medias (películas/series) asociadas con este género.
     * Representa el lado inverso de la relación muchos-a-muchos con la entidad Media.
     */
    @ManyToMany(mappedBy = "generos")
    @Builder.Default
    private Set<Media> medias = new HashSet<>();
    
    /**
     * Añade una media a este género y mantiene la consistencia de la relación bidireccional.
     * 
     * @param media La entidad Media que se añadirá a este género
     */
    public void addMedia(Media media) {
        medias.add(media);
        media.getGeneros().add(this);
    }
    
    /**
     * Elimina una media de este género y mantiene la consistencia de la relación bidireccional.
     * 
     * @param media La entidad Media que se eliminará de este género
     */
    public void removeMedia(Media media) {
        medias.remove(media);
        media.getGeneros().remove(this);
    }
}