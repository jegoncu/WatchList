package com.jesusgc.WatchList.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa la participación de una persona en un elemento
 * multimedia.
 * Utiliza clave compuesta para relacionar Media, Persona y el rol desempeñado.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "credito")
@Getter
@Setter
@NoArgsConstructor
public class Credito {

  /**
   * Clave compuesta que incluye IDs de media, persona y rol.
   */
  @EmbeddedId
  private CreditoId id;

  /**
   * Elemento multimedia en el que participa la persona.
   */
  @ManyToOne
  @MapsId("mediaId")
  @JoinColumn(name = "id_media", referencedColumnName = "id_media")
  private Media media;

  /**
   * Persona que participa en el elemento multimedia.
   */
  @ManyToOne
  @MapsId("personaId")
  @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
  private Persona persona;

  /**
   * Nombre del personaje interpretado.
   * Campo opcional, principalmente para actores.
   */
  @Column(name = "personaje")
  private String personaje;

  /**
   * Constructor que crea un crédito con media, persona y rol.
   *
   * @param media   El elemento multimedia
   * @param persona La persona que participa
   * @param rol     El rol desempeñado (actor, director, etc.)
   */
  public Credito(Media media, Persona persona, String rol) {
    this.media = media;
    this.persona = persona;
    if (media != null && persona != null && media.getId() != null && persona.getId() != null) {
      this.id = new CreditoId(media.getId(), persona.getId(), rol);
    } else {
      this.id = new CreditoId();
      if (rol != null)
        this.id.setRol(rol);
    }
  }

  /**
   * Obtiene el rol desempeñado por la persona.
   *
   * @return El rol o null si no está definido
   */
  public String getRol() {
    return this.id != null ? this.id.getRol() : null;
  }

  /**
   * Establece el rol desempeñado por la persona.
   *
   * @param rol El rol a establecer
   */
  public void setRol(String rol) {
    if (this.id == null) {
      this.id = new CreditoId();
    }
    this.id.setRol(rol);
  }
}
