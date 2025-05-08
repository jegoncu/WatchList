package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Long> {
}