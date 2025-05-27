package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Comentario;
import com.jesusgc.WatchList.model.Media;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.repository.ComentarioRepository;
import com.jesusgc.WatchList.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void eliminarComentario(Long comentarioId, Usuario usuario) {
        Optional<Comentario> comentarioOpt = comentarioRepository.findById(comentarioId);

        if (comentarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Comentario no encontrado");
        }

        Comentario comentario = comentarioOpt.get();

        if (!comentario.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permisos para eliminar este comentario");
        }

        comentarioRepository.delete(comentario);
    }
}
