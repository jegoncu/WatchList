package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long>, JpaSpecificationExecutor<Pelicula> {
    List<Pelicula> findByTituloContainingIgnoreCase(String titulo);
}
