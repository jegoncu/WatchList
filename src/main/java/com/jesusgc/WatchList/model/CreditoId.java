package com.jesusgc.WatchList.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CreditoId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_media")
    private Long mediaId;

    @Column(name = "id_persona")
    private Long personaId;

    @Column(name = "rol")
    private String rol; 

    public CreditoId() {
    }

    public CreditoId(Long mediaId, Long personaId, String rol) {
        this.mediaId = mediaId;
        this.personaId = personaId;
        this.rol = rol;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditoId that = (CreditoId) o;
        return Objects.equals(mediaId, that.mediaId) &&
               Objects.equals(personaId, that.personaId) &&
               Objects.equals(rol, that.rol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaId, personaId, rol);
    }
}
