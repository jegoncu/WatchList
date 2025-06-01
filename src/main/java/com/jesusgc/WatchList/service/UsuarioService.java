package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de usuarios del sistema.
 * Maneja operaciones de registro, autenticación y administración de cuentas.
 *
 * @author Jesús González Cuenca
 */
@Service
public class UsuarioService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  /**
   * Registra un nuevo usuario como usuario regular (no administrador).
   *
   * @param email       Dirección de correo electrónico del usuario
   * @param nombre      Nombre del usuario
   * @param contrasenia Contraseña en texto plano
   * @param esPublico   Indica si el perfil es público
   * @return Usuario registrado
   */
  public Usuario registrar(String email, String nombre, String contrasenia, boolean esPublico) {
    return registrar(email, nombre, contrasenia, esPublico, false);
  }

  /**
   * Registra un nuevo usuario con opción de asignar privilegios de administrador.
   *
   * @param email       Dirección de correo electrónico del usuario
   * @param nombre      Nombre del usuario
   * @param contrasenia Contraseña en texto plano (será hasheada)
   * @param esPublico   Indica si el perfil es público
   * @param esAdmin     Indica si el usuario tiene privilegios de administrador
   * @return Usuario registrado
   * @throws IllegalArgumentException si el email ya está registrado
   */
  @Transactional
  public Usuario registrar(String email, String nombre, String contrasenia, boolean esPublico, boolean esAdmin) {
    if (usuarioRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("El email ya está registrado");
    }

    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setNombre(nombre);

    // Hashear la contraseña antes de guardarla
    String hashedPassword = BCrypt.hashpw(contrasenia, BCrypt.gensalt());
    usuario.setContrasenia(hashedPassword);

    usuario.setEsPublico(esPublico);
    usuario.setEsAdmin(esAdmin);

    return usuarioRepository.save(usuario);
  }

  /**
   * Autentica un usuario con email y contraseña.
   *
   * @param email       Dirección de correo electrónico
   * @param contrasenia Contraseña en texto plano
   * @return Usuario autenticado
   * @throws IllegalArgumentException si las credenciales son incorrectas
   */
  public Usuario login(String email, String contrasenia) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

    if (usuarioOpt.isEmpty()) {
      throw new IllegalArgumentException("Email o contraseña incorrectos");
    }

    Usuario usuario = usuarioOpt.get();
    if (!BCrypt.checkpw(contrasenia, usuario.getContrasenia())) {
      throw new IllegalArgumentException("Email o contraseña incorrectos");
    }

    return usuario;
  }

  /**
   * Busca un usuario por su ID.
   *
   * @param id ID del usuario
   * @return Usuario encontrado
   * @throws IllegalArgumentException si el usuario no existe
   */
  @Transactional(readOnly = true)
  public Usuario findById(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
  }

  /**
   * Busca un usuario por su ID de forma opcional.
   *
   * @param id ID del usuario
   * @return Optional conteniendo el usuario si existe
   */
  @Transactional(readOnly = true)
  public Optional<Usuario> findByIdOptional(Long id) {
    return usuarioRepository.findById(id);
  }

  /**
   * Obtiene todos los usuarios del sistema.
   *
   * @return Lista de todos los usuarios
   */
  @Transactional(readOnly = true)
  public List<Usuario> findAll() {
    return usuarioRepository.findAll();
  }

  /**
   * Elimina un usuario por su ID.
   *
   * @param id ID del usuario a eliminar
   * @throws IllegalArgumentException si el usuario no existe
   */
  @Transactional
  public void deleteById(Long id) {
    if (!usuarioRepository.existsById(id)) {
      throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
    }
    usuarioRepository.deleteById(id);
  }

  /**
   * Actualiza los datos de un usuario (operación administrativa).
   *
   * @param id               ID del usuario a actualizar
   * @param nombre           Nuevo nombre
   * @param email            Nuevo email
   * @param nuevaContrasenia Nueva contraseña (opcional)
   * @param esPublico        Nueva visibilidad del perfil
   * @param esAdmin          Nuevos privilegios de administrador
   * @return Usuario actualizado
   * @throws IllegalArgumentException si el usuario no existe o el email ya está
   *                                  en uso
   */
  @Transactional
  public Usuario updateUsuario(Long id, String nombre, String email, String nuevaContrasenia,
      Boolean esPublico, Boolean esAdmin) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
    if (usuarioOpt.isEmpty()) {
      throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
    }

    Usuario usuario = usuarioOpt.get();

    // Verificar que el nuevo email no esté en uso por otro usuario
    if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("El email ya está registrado por otro usuario");
    }

    usuario.setNombre(nombre);
    usuario.setEmail(email);
    usuario.setEsPublico(esPublico);
    usuario.setEsAdmin(esAdmin);

    // Solo actualizar contraseña si se proporciona una nueva
    if (nuevaContrasenia != null && !nuevaContrasenia.trim().isEmpty()) {
      String hashedPassword = BCrypt.hashpw(nuevaContrasenia, BCrypt.gensalt());
      usuario.setContrasenia(hashedPassword);
    }

    return usuarioRepository.save(usuario);
  }

  /**
   * Actualiza el perfil de un usuario (operación del propio usuario).
   *
   * @param usuarioId           ID del usuario
   * @param nombre              Nuevo nombre
   * @param email               Nuevo email
   * @param contrasenaActual    Contraseña actual para verificación
   * @param nuevaContrasena     Nueva contraseña (opcional)
   * @param confirmarContrasena Confirmación de la nueva contraseña
   * @param esPublico           Nueva visibilidad del perfil
   * @return Usuario actualizado
   * @throws IllegalArgumentException si la contraseña actual es incorrecta o hay
   *                                  errores de validación
   */
  @Transactional
  public Usuario actualizarPerfil(Long usuarioId, String nombre, String email,
      String contrasenaActual, String nuevaContrasena,
      String confirmarContrasena, Boolean esPublico) {

    Usuario usuario = findById(usuarioId);

    // Verificar contraseña actual
    if (!BCrypt.checkpw(contrasenaActual, usuario.getContrasenia())) {
      throw new IllegalArgumentException("La contraseña actual es incorrecta");
    }

    // Verificar que el nuevo email no esté en uso
    if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("El email ya está registrado por otro usuario");
    }

    // Validar nueva contraseña si se proporciona
    if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
      if (nuevaContrasena.length() < 6) {
        throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
      }
      if (!nuevaContrasena.equals(confirmarContrasena)) {
        throw new IllegalArgumentException("Las contraseñas no coinciden");
      }
      String hashedPassword = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());
      usuario.setContrasenia(hashedPassword);
    }

    usuario.setNombre(nombre);
    usuario.setEmail(email);
    usuario.setEsPublico(esPublico);

    return usuarioRepository.save(usuario);
  }

  /**
   * Elimina la cuenta del propio usuario previa verificación de contraseña.
   *
   * @param usuarioId              ID del usuario
   * @param contrasenaConfirmacion Contraseña para confirmar la eliminación
   * @throws IllegalArgumentException si la contraseña es incorrecta
   */
  @Transactional
  public void eliminarCuentaPropia(Long usuarioId, String contrasenaConfirmacion) {
    Usuario usuario = findById(usuarioId);

    if (!BCrypt.checkpw(contrasenaConfirmacion, usuario.getContrasenia())) {
      throw new IllegalArgumentException("Contraseña incorrecta");
    }

    usuarioRepository.delete(usuario);
  }
}
