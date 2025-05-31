package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Credito;
import com.jesusgc.WatchList.model.CreditoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de créditos en la base de datos.
 * Proporciona operaciones CRUD para las participaciones de personas en elementos multimedia.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface CreditoRepository extends JpaRepository<Credito, CreditoId> {

}
