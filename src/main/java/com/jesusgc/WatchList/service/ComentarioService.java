package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Comentario;
import com.jesusgc.WatchList.model.Media;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.repository.ComentarioRepository;
import com.jesusgc.WatchList.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository,
            UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Comentario guardarComentario(Media media, String textoComentario, int puntuacion, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        if (media == null) {
            throw new IllegalArgumentException("El objeto Media no puede ser nulo.");
        }

        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setMedia(media);
        comentario.setTexto(textoComentario);
        comentario.setPuntuacion(puntuacion);

        return comentarioRepository.save(comentario);
    }

    public List<Comentario> obtenerComentariosPorMediaId(Long mediaId) {
        return comentarioRepository.findByMediaIdOrderByFechaDesc(mediaId);
    }
}