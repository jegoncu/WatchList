package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByMediaIdOrderByFechaDesc(Long mediaId);

    Optional<Comentario> findByUsuarioIdAndMediaId(Long usuarioId, Long mediaId);
}
