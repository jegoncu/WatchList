package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de plataformas de streaming en la base de datos.
 * Proporciona operaciones CRUD para las plataformas de distribución multimedia.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {
}
