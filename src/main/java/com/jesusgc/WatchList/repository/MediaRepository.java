package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de elementos multimedia en la base de datos.
 * Proporciona operaciones CRUD para películas y series de forma polimórfica.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

}
