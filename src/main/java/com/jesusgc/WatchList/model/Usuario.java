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
 * Entidad que representa un usuario registrado.
 * Los usuarios pueden crear listas, comentar y puntuar elementos multimedia.
 *
 * @author Jesús González Cuenca
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "comentarios", "listas" })
public class Usuario {

  /**
   * Identificador único del usuario.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_usuario")
  private Long id;

  /**
   * Dirección de correo electrónico del usuario.
   * Debe ser única en el sistema.
   */
  @NotBlank(message = "El email no puede estar vacío")
  @Email(message = "El formato del email no es válido")
  @Column(name = "email", unique = true, nullable = false)
  private String email;

  /**
   * Nombre del usuario.
   */
  @NotBlank(message = "El nombre no puede estar vacío")
  @Column(name = "nombre", nullable = false)
  private String nombre;

  /**
   * Contraseña del usuario hasheada.
   * Debe tener al menos 6 caracteres.
   */
  @NotBlank(message = "La contraseña no puede estar vacía")
  @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
  @Column(name = "contrasenia", nullable = false)
  private String contrasenia;

  /**
   * Indica si el perfil del usuario es público.
   * Por defecto es true.
   */
  @Column(name = "es_publico")
  private Boolean esPublico;

  /**
   * Indica si el usuario tiene privilegios de administrador.
   * Por defecto es false.
   */
  @Column(name = "es_admin")
  private Boolean esAdmin;

  /**
   * Colección de comentarios realizados por el usuario.
   */
  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<Comentario> comentarios = new HashSet<>();

  /**
   * Colección de listas creadas por el usuario.
   */
  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<Lista> listas = new HashSet<>();

  /**
   * Añade un comentario a la colección del usuario.
   *
   * @param comentario El comentario a añadir
   */
  public void addComentario(Comentario comentario) {
    comentarios.add(comentario);
    comentario.setUsuario(this);
  }

  /**
   * Elimina un comentario de la colección del usuario.
   *
   * @param comentario El comentario a remover
   */
  public void removeComentario(Comentario comentario) {
    comentarios.remove(comentario);
    comentario.setUsuario(null);
  }

  /**
   * Añade una lista a la colección del usuario.
   *
   * @param lista La lista a añadir
   */
  public void addLista(Lista lista) {
    listas.add(lista);
    lista.setUsuario(this);
  }

  /**
   * Elimina una lista de la colección del usuario.
   *
   * @param lista La lista a remover
   */
  public void removeLista(Lista lista) {
    listas.remove(lista);
    lista.setUsuario(null);
  }

  /**
   * Establece valores por defecto antes de persistir el usuario.
   * esPublico = true, esAdmin = false si no están definidos.
   */
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
