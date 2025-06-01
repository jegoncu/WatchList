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
 * Entidad que representa una persona del mundo del entretenimiento.
 * Incluye actores, directores, guionistas y otros profesionales de la
 * industria.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "persona")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "participaciones" })
public class Persona {

  /**
   * Identificador único de la persona.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_persona")
  private Long id;

  /**
   * Nombre completo de la persona.
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
   * Biografía o descripción de la persona.
   * Campo opcional de texto extenso.
   */
  @Column(name = "biografia", columnDefinition = "TEXT")
  private String biografia;

  /**
   * URL de la foto de la persona.
   * Campo opcional.
   */
  @Column(name = "foto_url")
  private String fotoUrl;

  /**
   * Colección de créditos/participaciones de la persona en diferentes medios.
   */
  @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<Credito> participaciones = new HashSet<>();

  /**
   * Añade una participación de la persona en un elemento multimedia.
   *
   * @param media El elemento multimedia
   * @param rol   El rol desempeñado (actor, director, etc.)
   */
  public void addParticipacion(Media media, String rol) {
    Credito credito = new Credito(media, this, rol);
    this.participaciones.add(credito);
  }

  /**
   * Remueve una participación específica de la persona.
   *
   * @param credito El crédito/participación a remover
   */
  public void removeParticipacion(Credito credito) {
    this.participaciones.remove(credito);
    credito.setPersona(null);
    credito.setMedia(null);
  }
}
