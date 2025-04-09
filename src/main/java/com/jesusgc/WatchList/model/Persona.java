package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa una persona relacionada con medias (actor, director, etc.).
 * Esta clase está mapeada a la tabla "persona" en la base de datos.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity
@Table(name = "persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"medias"})
public class Persona {

    /**
     * Identificador único de la persona.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long id;
    
    /**
     * Nombre de la persona.
     * No puede estar vacío.
     */
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    /**
     * Fecha de nacimiento de la persona.
     * Debe ser una fecha en el pasado.
     */
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nac")
    private LocalDate fechaNac;
    
    /**
     * Biografía de la persona.
     * Texto con información biográfica sobre la persona.
     */
    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia;
    
    /**
     * Conjunto de medias (películas/series) en las que ha participado esta persona.
     * Representa el lado inverso de la relación muchos-a-muchos con la entidad Media.
     */
    @ManyToMany(mappedBy = "personas")
    @Builder.Default
    private Set<Media> medias = new HashSet<>();
    
    /**
     * Añade una media a esta persona y mantiene la consistencia de la relación bidireccional.
     * 
     * @param media La entidad Media que se añadirá a esta persona
     */
    public void addMedia(Media media) {
        medias.add(media);
        media.getPersonas().add(this);
    }
    
    /**
     * Elimina una media de esta persona y mantiene la consistencia de la relación bidireccional.
     * 
     * @param media La entidad Media que se eliminará de esta persona
     */
    public void removeMedia(Media media) {
        medias.remove(media);
        media.getPersonas().remove(this);
    }
}