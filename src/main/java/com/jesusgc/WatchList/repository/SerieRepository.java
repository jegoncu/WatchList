package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long>, JpaSpecificationExecutor<Serie> {
    List<Serie> findByTituloContainingIgnoreCase(String titulo);
}
