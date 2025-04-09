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

/**
 * Entidad que representa un usuario de la aplicación.
 * Esta clase está mapeada a la tabla "usuario" en la base de datos.
 * 
 * @author jesusgc
 * @version 0.1
 * @since 2023-10-01
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"comentarios", "listas"})
public class Usuario {

    /**
     * Identificador único del usuario.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;
    
    /**
     * Email del usuario.
     * Debe ser único y tener un formato válido de correo electrónico.
     */
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    /**
     * Nombre del usuario.
     * No puede estar vacío.
     */
    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    /**
     * Contraseña del usuario.
     * Debe tener al menos 6 caracteres y no puede estar vacía.
     */
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(name = "contrasenia", nullable = false)
    private String contrasenia;
    
    /**
     * Indica si el perfil del usuario es público.
     * Se establece automáticamente como público si no se proporciona.
     */
    @Column(name = "es_publico")
    private Boolean esPublico;
    
    /**
     * Indica si el usuario tiene permisos de administrador.
     * Se establece automáticamente como falso si no se proporciona.
     */
    @Column(name = "es_admin")
    private Boolean esAdmin;
    
    /**
     * Conjunto de comentarios realizados por este usuario.
     * Representa el lado uno en la relación uno-a-muchos con la entidad Comentario.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Comentario> comentarios = new HashSet<>();
    
    /**
     * Conjunto de listas creadas por este usuario.
     * Representa el lado uno en la relación uno-a-muchos con la entidad Lista.
     */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Lista> listas = new HashSet<>();
    
    /**
     * Añade un comentario a este usuario y mantiene la consistencia de la relación bidireccional.
     * 
     * @param comentario El comentario que se añadirá a este usuario
     */
    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
        comentario.setUsuario(this);
    }
    
    /**
     * Elimina un comentario de este usuario y mantiene la consistencia de la relación bidireccional.
     * 
     * @param comentario El comentario que se eliminará de este usuario
     */
    public void removeComentario(Comentario comentario) {
        comentarios.remove(comentario);
        comentario.setUsuario(null);
    }
    
    /**
     * Añade una lista a este usuario y mantiene la consistencia de la relación bidireccional.
     * 
     * @param lista La lista que se añadirá a este usuario
     */
    public void addLista(Lista lista) {
        listas.add(lista);
        lista.setUsuario(this);
    }
    
    /**
     * Elimina una lista de este usuario y mantiene la consistencia de la relación bidireccional.
     * 
     * @param lista La lista que se eliminará de este usuario
     */
    public void removeLista(Lista lista) {
        listas.remove(lista);
        lista.setUsuario(null);
    }
    
    /**
     * Método que se ejecuta automáticamente antes de persistir un usuario.
     * Establece valores por defecto para los campos esPublico y esAdmin si no se han proporcionado.
     */
    @PrePersist
    protected void onCreate() {
        if (esPublico == null) {
            esPublico = true; // Por defecto, los perfiles son públicos
        }
        if (esAdmin == null) {
            esAdmin = false; // Por defecto, los usuarios no son administradores
        }
    }
}