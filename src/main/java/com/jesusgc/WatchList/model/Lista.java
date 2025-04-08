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

@Entity
@Table(name = "lista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"usuario", "medias"})
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lista")
    private Long id;
    
    @NotNull(message = "El usuario no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @NotBlank(message = "El título no puede estar vacío")
    @Column(name = "titulo", nullable = false)
    private String titulo;
    
    @Column(name = "es_publica")
    private Boolean esPublica;
    
    @ManyToMany
    @Builder.Default
    @JoinTable(
        name = "lista_media",
        joinColumns = @JoinColumn(name = "id_lista"),
        inverseJoinColumns = @JoinColumn(name = "id_media")
    )
    private Set<Media> medias = new HashSet<>();
    
    // Metodos auxiliares para relaciones bidireccionales
    public void addMedia(Media media) {
        medias.add(media);
        media.getListas().add(this);
    }
    
    public void removeMedia(Media media) {
        medias.remove(media);
        media.getListas().remove(this);
    }
    
    @PrePersist
    protected void onCreate() {
        if (esPublica == null) {
            esPublica = true; // Por defecto, las listas son públicas
        }
    }
}