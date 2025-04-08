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

@Entity
@Table(name = "plataforma")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"medias"})
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plataforma")
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;
    
    @Column(name = "logo")
    private String logo;
    
    @ManyToMany(mappedBy = "plataformas")
    @Builder.Default
    private Set<Media> medias = new HashSet<>();
    
    // Metodos auxiliares para relaciones bidireccionales
    public void addMedia(Media media) {
        medias.add(media);
        media.getPlataformas().add(this);
    }
    
    public void removeMedia(Media media) {
        medias.remove(media);
        media.getPlataformas().remove(this);
    }
}