package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de comentarios en la base de datos.
 * Proporciona operaciones CRUD y consultas personalizadas para comentarios.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

  /**
   * Busca todos los comentarios de un elemento multimedia ordenados por fecha
   * descendente.
   *
   * @param mediaId ID del elemento multimedia
   * @return Lista de comentarios ordenados del más reciente al más antiguo
   */
  List<Comentario> findByMediaIdOrderByFechaDesc(Long mediaId);

  /**
   * Busca el comentario específico de un usuario sobre un elemento multimedia.
   *
   * @param usuarioId ID del usuario
   * @param mediaId   ID del elemento multimedia
   * @return Optional conteniendo el comentario si existe
   */
  Optional<Comentario> findByUsuarioIdAndMediaId(Long usuarioId, Long mediaId);
}
