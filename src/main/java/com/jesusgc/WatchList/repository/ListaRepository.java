package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de listas de usuarios en la base de datos.
 * Proporciona operaciones CRUD y consultas para listas públicas y privadas.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface ListaRepository extends JpaRepository<Lista, Long> {

  /**
   * Busca todas las listas de un usuario específico.
   *
   * @param usuario Usuario propietario de las listas
   * @return Lista de todas las listas del usuario
   */
  List<Lista> findByUsuario(Usuario usuario);

  /**
   * Busca todas las listas públicas del sistema.
   *
   * @return Lista de listas públicas
   */
  List<Lista> findByEsPublicaTrue();

  /**
   * Busca una lista específica que pertenezca a un usuario.
   *
   * @param id      ID de la lista
   * @param usuario Usuario propietario
   * @return Optional conteniendo la lista si existe y pertenece al usuario
   */
  Optional<Lista> findByIdAndUsuario(Long id, Usuario usuario);

  /**
   * Busca listas públicas por título que contenga el texto especificado.
   *
   * @param titulo Texto a buscar en el título (ignora mayúsculas/minúsculas)
   * @return Lista de listas públicas que coinciden con la búsqueda
   */
  List<Lista> findByTituloContainingIgnoreCaseAndEsPublicaTrue(String titulo);
}
