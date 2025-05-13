package com.jesusgc.WatchList.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "comentarios", "listas" })
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(name = "contrasenia", nullable = false)
    private String contrasenia;

    @Column(name = "es_publico")
    private Boolean esPublico;

    @Column(name = "es_admin")
    private Boolean esAdmin;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Comentario> comentarios = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Lista> listas = new HashSet<>();

    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setUsuario(this);
    }

    public void removeComentario(Comentario comentario) {
        comentarios.remove(comentario);
        comentario.setUsuario(null);
    }

    public void addLista(Lista lista) {
        listas.add(lista);
        lista.setUsuario(this);
    }

    public void removeLista(Lista lista) {
        listas.remove(lista);
        lista.setUsuario(null);
    }

    @PrePersist
    protected void onCreate() {
        if (esPublico == null) {
            esPublico = true;
        }
        if (esAdmin == null) {
            esAdmin = false;
        }
    }
}