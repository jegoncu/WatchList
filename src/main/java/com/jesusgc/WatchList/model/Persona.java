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
@ToString(exclude = { "participaciones" })
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

    @Column(name = "foto_url")
    private String fotoUrl;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Credito> participaciones = new HashSet<>();

    public void addParticipacion(Media media, String rol) {
        Credito credito = new Credito(media, this, rol);
        this.participaciones.add(credito);
    }

    public void removeParticipacion(Credito credito) {
        this.participaciones.remove(credito);
        credito.setPersona(null);
        credito.setMedia(null);
    }
}