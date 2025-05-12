package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Credito;
import com.jesusgc.WatchList.model.CreditoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, CreditoId> {

}
