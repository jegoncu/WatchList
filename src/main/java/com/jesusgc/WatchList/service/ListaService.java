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

/**
 * Servicio para la gestión de listas personalizadas de usuarios.
 * Maneja las operaciones de creación, consulta y modificación de listas de
 * multimedia.
 *
 * @author Jesús González Cuenca
 */
@Service
public class ListaService {

  private final ListaRepository listaRepository;
  private final MediaRepository mediaRepository;

  /**
   * Constructor del servicio con inyección de dependencias.
   *
   * @param listaRepository Repositorio de listas
   * @param mediaRepository Repositorio de elementos multimedia
   */
  public ListaService(ListaRepository listaRepository, MediaRepository mediaRepository) {
    this.listaRepository = listaRepository;
    this.mediaRepository = mediaRepository;
  }

  /**
   * Crea una nueva lista para un usuario.
   *
   * @param titulo    Título de la lista
   * @param esPublica Indica si la lista es pública o privada
   * @param usuario   Usuario propietario de la lista
   * @return Lista creada y guardada
   */
  @Transactional
  public Lista crearLista(String titulo, Boolean esPublica, Usuario usuario) {
    Lista nuevaLista = new Lista();
    nuevaLista.setTitulo(titulo);
    nuevaLista.setEsPublica(esPublica);
    usuario.addLista(nuevaLista);
    return listaRepository.save(nuevaLista);
  }

  /**
   * Obtiene todas las listas de un usuario específico.
   *
   * @param usuario Usuario del que obtener las listas
   * @return Lista de listas del usuario
   */
  @Transactional(readOnly = true)
  public List<Lista> obtenerListasPorUsuario(Usuario usuario) {
    return listaRepository.findByUsuario(usuario);
  }

  /**
   * Obtiene todas las listas públicas del sistema.
   *
   * @return Lista de listas públicas
   */
  @Transactional(readOnly = true)
  public List<Lista> obtenerListasPublicas() {
    return listaRepository.findByEsPublicaTrue();
  }

  /**
   * Obtiene una lista específica que pertenezca al usuario.
   *
   * @param listaId ID de la lista
   * @param usuario Usuario propietario
   * @return Lista encontrada
   * @throws IllegalArgumentException si la lista no existe o no pertenece al
   *                                  usuario
   */
  @Transactional(readOnly = true)
  public Lista obtenerListaPorIdYUsuario(Long listaId, Usuario usuario) {
    return listaRepository.findByIdAndUsuario(listaId, usuario)
        .orElseThrow(() -> new IllegalArgumentException(
            "Lista no encontrada (ID: " + listaId + ") o no pertenece al usuario " + usuario.getEmail()));
  }

  /**
   * Busca una lista por su ID.
   *
   * @param listaId ID de la lista
   * @return Optional conteniendo la lista si existe
   */
  @Transactional(readOnly = true)
  public Optional<Lista> findById(Long listaId) {
    return listaRepository.findById(listaId);
  }

  /**
   * Añade un elemento multimedia a una lista del usuario.
   *
   * @param listaId ID de la lista
   * @param mediaId ID del elemento multimedia
   * @param usuario Usuario propietario de la lista
   * @return Lista actualizada
   * @throws IllegalArgumentException si la lista o el media no existen
   */
  @Transactional
  public Lista agregarMediaALista(Long listaId, Long mediaId, Usuario usuario) {
    Lista lista = obtenerListaPorIdYUsuario(listaId, usuario);
    Media media = mediaRepository.findById(mediaId)
        .orElseThrow(() -> new IllegalArgumentException("Media item no encontrado con ID: " + mediaId));
    lista.addMedia(media);
    return listaRepository.save(lista);
  }

  /**
   * Remueve un elemento multimedia de una lista del usuario.
   *
   * @param listaId ID de la lista
   * @param mediaId ID del elemento multimedia
   * @param usuario Usuario propietario de la lista
   * @return Lista actualizada
   * @throws IllegalArgumentException si la lista o el media no existen
   */
  @Transactional
  public Lista quitarMediaDeLista(Long listaId, Long mediaId, Usuario usuario) {
    Lista lista = obtenerListaPorIdYUsuario(listaId, usuario);
    Media media = mediaRepository.findById(mediaId)
        .orElseThrow(() -> new IllegalArgumentException("Media item no encontrado con ID: " + mediaId));
    lista.removeMedia(media);
    return listaRepository.save(lista);
  }

  /**
   * Elimina una lista del usuario.
   *
   * @param listaId ID de la lista a eliminar
   * @param usuario Usuario propietario de la lista
   * @throws IllegalArgumentException si la lista no existe o no pertenece al
   *                                  usuario
   */
  @Transactional
  public void eliminarLista(Long listaId, Usuario usuario) {
    Lista lista = obtenerListaPorIdYUsuario(listaId, usuario);
    listaRepository.delete(lista);
  }

  /**
   * Busca listas públicas por título que contenga el texto especificado.
   *
   * @param titulo Texto a buscar en el título
   * @return Lista de listas públicas que coinciden con la búsqueda
   */
  @Transactional(readOnly = true)
  public List<Lista> buscarPorTitulo(String titulo) {
    if (titulo == null || titulo.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return listaRepository.findByTituloContainingIgnoreCaseAndEsPublicaTrue(titulo.trim());
  }

  /**
   * Obtiene todas las listas del sistema.
   *
   * @return Lista de todas las listas
   */
  @Transactional(readOnly = true)
  public List<Lista> findAll() {
    return listaRepository.findAll();
  }

  /**
   * Elimina una lista por su ID.
   *
   * @param id ID de la lista a eliminar
   * @throws IllegalArgumentException si la lista no existe
   */
  @Transactional
  public void deleteById(Long id) {
    if (!listaRepository.existsById(id)) {
      throw new IllegalArgumentException("Lista no encontrada con ID: " + id);
    }
    listaRepository.deleteById(id);
  }

  /**
   * Actualiza los datos de una lista existente.
   *
   * @param id        ID de la lista a actualizar
   * @param titulo    Nuevo título de la lista
   * @param esPublica Nueva visibilidad de la lista
   * @param usuario   Nuevo usuario propietario
   * @return Lista actualizada
   * @throws IllegalArgumentException si la lista no existe
   */
  @Transactional
  public Lista updateLista(Long id, String titulo, Boolean esPublica, Usuario usuario) {
    Optional<Lista> listaOpt = listaRepository.findById(id);
    if (listaOpt.isEmpty()) {
      throw new IllegalArgumentException("Lista no encontrada con ID: " + id);
    }

    Lista lista = listaOpt.get();
    lista.setTitulo(titulo);
    lista.setEsPublica(esPublica);
    lista.setUsuario(usuario);

    return listaRepository.save(lista);
  }
}
