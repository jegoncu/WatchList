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

@Entity
@Table(name = "credito")
@Getter
@Setter
@NoArgsConstructor
public class Credito {

    @EmbeddedId
    private CreditoId id;

    @ManyToOne
    @MapsId("mediaId")
    @JoinColumn(name = "id_media", referencedColumnName = "id_media")
    private Media media;

    @ManyToOne
    @MapsId("personaId")
    @JoinColumn(name = "id_persona", referencedColumnName = "id_persona")
    private Persona persona;

    @Column(name = "personaje")
    private String personaje;

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

    public String getRol() {
        return this.id != null ? this.id.getRol() : null;
    }

    public void setRol(String rol) {
        if (this.id == null) {
            this.id = new CreditoId();
        }
        this.id.setRol(rol);
    }
}
