package com.jesusgc.WatchList.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Clave compuesta para la entidad Credito.
 * Combina los IDs de Media, Persona y el rol para formar una clave única.
 *
 * @author Jesús González Cuenca
 */
@Embeddable
public class CreditoId implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Identificador del elemento multimedia.
   */
  @Column(name = "id_media")
  private Long mediaId;

  /**
   * Identificador de la persona.
   */
  @Column(name = "id_persona")
  private Long personaId;

  /**
   * Rol desempeñado por la persona en el elemento multimedia.
   */
  @Column(name = "rol")
  private String rol;

  /**
   * Constructor por defecto.
   */
  public CreditoId() {
  }

  /**
   * Constructor con todos los campos.
   *
   * @param mediaId   ID del elemento multimedia
   * @param personaId ID de la persona
   * @param rol       Rol desempeñado
   */
  public CreditoId(Long mediaId, Long personaId, String rol) {
    this.mediaId = mediaId;
    this.personaId = personaId;
    this.rol = rol;
  }

  /**
   * Obtiene el ID del elemento multimedia.
   *
   * @return ID del media
   */
  public Long getMediaId() {
    return mediaId;
  }

  /**
   * Establece el ID del elemento multimedia.
   *
   * @param mediaId ID del media
   */
  public void setMediaId(Long mediaId) {
    this.mediaId = mediaId;
  }

  /**
   * Obtiene el ID de la persona.
   *
   * @return ID de la persona
   */
  public Long getPersonaId() {
    return personaId;
  }

  /**
   * Establece el ID de la persona.
   *
   * @param personaId ID de la persona
   */
  public void setPersonaId(Long personaId) {
    this.personaId = personaId;
  }

  /**
   * Obtiene el rol desempeñado.
   *
   * @return El rol
   */
  public String getRol() {
    return rol;
  }

  /**
   * Establece el rol desempeñado.
   *
   * @param rol El rol
   */
  public void setRol(String rol) {
    this.rol = rol;
  }

  /**
   * Compara este objeto con otro para determinar igualdad.
   *
   * @param o Objeto a comparar
   * @return true si son iguales
   */
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CreditoId that = (CreditoId) o;
    return Objects.equals(mediaId, that.mediaId) &&
        Objects.equals(personaId, that.personaId) &&
        Objects.equals(rol, that.rol);
  }

  /**
   * Genera el código hash del objeto.
   *
   * @return Código hash
   */
  @Override
  public int hashCode() {
    return Objects.hash(mediaId, personaId, rol);
  }
}
