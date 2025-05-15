package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {
    // No es necesario añadir métodos personalizados si usamos solo operaciones CRUD básicas
}