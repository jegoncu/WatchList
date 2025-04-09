package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa una lista de medias creada por un usuario.
 * Esta clase está mapeada a la tabla "lista" en la base de datos.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity
@Table(name = "lista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"usuario", "medias"})
public class Lista {

    /**
     * Identificador único de la lista.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lista")
    private Long id;
    
    /**
     * Usuario que ha creado la lista.
     * Representa el lado muchos en la relación muchos-a-uno con la entidad Usuario.
     */
    @NotNull(message = "El usuario no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    /**
     * Título de la lista.
     * No puede estar vacío.
     */
    @NotBlank(message = "El título no puede estar vacío")
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    /**
     * Indica si la lista es pública o privada.
     * Se establece automáticamente como pública si no se proporciona.
     */
    @Column(name = "es_publica")
    private Boolean esPublica;
    
    /**
     * Conjunto de medias (películas/series) añadidas a esta lista.
     * Representa la relación muchos-a-muchos con la entidad Media.
     */
    @ManyToMany
    @Builder.Default
    @JoinTable(
        name = "lista_media",
        joinColumns = @JoinColumn(name = "id_lista"),
        inverseJoinColumns = @JoinColumn(name = "id_media")
    )
    private Set<Media> medias = new HashSet<>();
    
    /**
     * Añade una media a esta lista y mantiene la consistencia de la relación bidireccional.
     * 
     * @param media La entidad Media que se añadirá a esta lista
     */
    public void addMedia(Media media) {
        medias.add(media);
        media.getListas().add(this);
    }
    
    /**
     * Elimina una media de esta lista y mantiene la consistencia de la relación bidireccional.
     * 
     * @param media La entidad Media que se eliminará de esta lista
     */
    public void removeMedia(Media media) {
        medias.remove(media);
        media.getListas().remove(this);
    }
    
    /**
     * Método que se ejecuta automáticamente antes de persistir una lista.
     * Establece el valor por defecto para el campo esPublica si no se ha proporcionado.
     */
    @PrePersist
    protected void onCreate() {
        if (esPublica == null) {
            esPublica = true; // Por defecto, las listas son públicas
        }
    }
}