package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de usuarios en la base de datos.
 * Proporciona operaciones CRUD y consultas para autenticación y validación.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  /**
   * Busca un usuario por su dirección de correo electrónico.
   *
   * @param email Dirección de correo electrónico del usuario
   * @return Optional conteniendo el usuario si existe
   */
  Optional<Usuario> findByEmail(String email);

  /**
   * Verifica si existe un usuario con el email especificado.
   *
   * @param email Dirección de correo electrónico a verificar
   * @return true si el usuario existe
   */
  Boolean existsByEmail(String email);
}
