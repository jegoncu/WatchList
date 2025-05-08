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

@Entity
@Table(name = "persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "medias" })
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia;

    @ManyToMany(mappedBy = "personas")
    @Builder.Default
    private Set<Media> medias = new HashSet<>();

    public void addMedia(Media media) {
        medias.add(media);
        media.getPersonas().add(this);
    }

    public void removeMedia(Media media) {
        medias.remove(media);
        media.getPersonas().remove(this);
    }
}