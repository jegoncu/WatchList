package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de series en la base de datos.
 * Proporciona operaciones CRUD y consultas dinámicas avanzadas para series.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface SerieRepository extends JpaRepository<Serie, Long>, JpaSpecificationExecutor<Serie> {

    /**
     * Busca series por título que contenga el texto especificado.
     *
     * @param titulo Texto a buscar en el título (ignora mayúsculas/minúsculas)
     * @return Lista de series que coinciden con la búsqueda
     */
    List<Serie> findByTituloContainingIgnoreCase(String titulo);
}
