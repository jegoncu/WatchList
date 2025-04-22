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
@Table(name = "genero")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"medias"})
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero")
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;
    
    @ManyToMany(mappedBy = "generos")
    @Builder.Default
    private Set<Media> medias = new HashSet<>();
    
    public void addMedia(Media media) {
        medias.add(media);
        media.getGeneros().add(this);
    }
    
    public void removeMedia(Media media) {
        medias.remove(media);
        media.getGeneros().remove(this);
    }
}