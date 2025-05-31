package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de géneros en la base de datos.
 * Proporciona operaciones CRUD para los géneros cinematográficos y televisivos.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
}
