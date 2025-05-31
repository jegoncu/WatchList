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

/**
 * Servicio para la gestión de comentarios y puntuaciones de elementos multimedia.
 * Maneja las operaciones de creación, consulta y eliminación de comentarios.
 *
 * @author Jesús González Cuenca
 */
@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor del servicio con inyección de dependencias.
     *
     * @param comentarioRepository Repositorio de comentarios
     * @param usuarioRepository Repositorio de usuarios
     */
    public ComentarioService(ComentarioRepository comentarioRepository,
            UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Guarda un nuevo comentario con puntuación para un elemento multimedia.
     *
     * @param media Elemento multimedia a comentar
     * @param textoComentario Texto del comentario
     * @param puntuacion Puntuación del 1 al 5
     * @param usuarioId ID del usuario que comenta
     * @return Comentario guardado
     * @throws IllegalArgumentException si el usuario no existe o el media es nulo
     */
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

    /**
     * Obtiene todos los comentarios de un elemento multimedia ordenados por fecha.
     *
     * @param mediaId ID del elemento multimedia
     * @return Lista de comentarios ordenados del más reciente al más antiguo
     */
    public List<Comentario> obtenerComentariosPorMediaId(Long mediaId) {
        return comentarioRepository.findByMediaIdOrderByFechaDesc(mediaId);
    }

    /**
     * Elimina un comentario verificando que pertenezca al usuario.
     *
     * @param comentarioId ID del comentario a eliminar
     * @param usuario Usuario que solicita la eliminación
     * @throws IllegalArgumentException si el comentario no existe o no pertenece al usuario
     */
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
