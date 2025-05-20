package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaRepository extends JpaRepository<Lista, Long> {
    List<Lista> findByUsuario(Usuario usuario);
    List<Lista> findByEsPublicaTrue();
    Optional<Lista> findByIdAndUsuario(Long id, Usuario usuario);
}
