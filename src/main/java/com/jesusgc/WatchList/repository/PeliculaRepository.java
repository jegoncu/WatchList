package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de películas en la base de datos.
 * Proporciona operaciones CRUD y consultas dinámicas avanzadas para películas.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long>, JpaSpecificationExecutor<Pelicula> {

  /**
   * Busca películas por título que contenga el texto especificado.
   *
   * @param titulo Texto a buscar en el título (ignora mayúsculas/minúsculas)
   * @return Lista de películas que coinciden con la búsqueda
   */
  List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
}
