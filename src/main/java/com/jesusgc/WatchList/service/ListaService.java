package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.model.Media;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.repository.ListaRepository;
import com.jesusgc.WatchList.repository.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final MediaRepository mediaRepository;

    public ListaService(ListaRepository listaRepository, MediaRepository mediaRepository) {
        this.listaRepository = listaRepository;
        this.mediaRepository = mediaRepository;
    }

    @Transactional
    public Lista crearLista(String titulo, Boolean esPublica, Usuario usuario) {
        Lista nuevaLista = new Lista();
        nuevaLista.setTitulo(titulo);
        nuevaLista.setEsPublica(esPublica);
        usuario.addLista(nuevaLista);
        return listaRepository.save(nuevaLista);
    }

    @Transactional(readOnly = true)
    public List<Lista> obtenerListasPorUsuario(Usuario usuario) {
        return listaRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public List<Lista> obtenerListasPublicas() {
        return listaRepository.findByEsPublicaTrue();
    }

    @Transactional(readOnly = true)
    public Lista obtenerListaPorIdYUsuario(Long listaId, Usuario usuario) {
        return listaRepository.findByIdAndUsuario(listaId, usuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Lista no encontrada (ID: " + listaId + ") o no pertenece al usuario " + usuario.getEmail()));
    }

    @Transactional(readOnly = true)
    public Optional<Lista> findById(Long listaId) {
        return listaRepository.findById(listaId);
    }

    @Transactional
    public Lista agregarMediaALista(Long listaId, Long mediaId, Usuario usuario) {
        Lista lista = obtenerListaPorIdYUsuario(listaId, usuario);
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media item no encontrado con ID: " + mediaId));
        lista.addMedia(media);
        return listaRepository.save(lista);
    }

    @Transactional
    public Lista quitarMediaDeLista(Long listaId, Long mediaId, Usuario usuario) {
        Lista lista = obtenerListaPorIdYUsuario(listaId, usuario);
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media item no encontrado con ID: " + mediaId));
        lista.removeMedia(media);
        return listaRepository.save(lista);
    }

    @Transactional
    public void eliminarLista(Long listaId, Usuario usuario) {
        Lista lista = obtenerListaPorIdYUsuario(listaId, usuario);
        listaRepository.delete(lista);
    }

    @Transactional(readOnly = true)
    public List<Lista> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return listaRepository.findByTituloContainingIgnoreCaseAndEsPublicaTrue(titulo.trim());
    }
}
