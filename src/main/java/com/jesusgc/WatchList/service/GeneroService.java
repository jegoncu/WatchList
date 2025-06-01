package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Genero;
import com.jesusgc.WatchList.repository.GeneroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de géneros cinematográficos y televisivos.
 * Proporciona operaciones CRUD para los géneros del sistema.
 *
 * @author Jesús González Cuenca
 */
@Service
public class GeneroService {

  private final GeneroRepository generoRepository;

  /**
   * Constructor del servicio con inyección de dependencias.
   *
   * @param generoRepository Repositorio de géneros
   */
  public GeneroService(GeneroRepository generoRepository) {
    this.generoRepository = generoRepository;
  }

  /**
   * Obtiene todos los géneros disponibles.
   *
   * @return Lista de todos los géneros
   */
  public List<Genero> findAll() {
    return generoRepository.findAll();
  }

  /**
   * Busca un género por su ID.
   *
   * @param id ID del género
   * @return Optional conteniendo el género si existe
   */
  public Optional<Genero> findById(Long id) {
    return generoRepository.findById(id);
  }

  /**
   * Guarda un género nuevo o actualiza uno existente.
   *
   * @param genero Género a guardar
   * @return Género guardado
   */
  @Transactional
  public Genero save(Genero genero) {
    return generoRepository.save(genero);
  }

  /**
   * Elimina un género por su ID.
   *
   * @param id ID del género a eliminar
   * @throws IllegalArgumentException si el género no existe
   */
  @Transactional
  public void deleteById(Long id) {
    if (!generoRepository.existsById(id)) {
      throw new IllegalArgumentException("Género con ID " + id + " no encontrado.");
    }
    generoRepository.deleteById(id);
  }

  /**
   * Obtiene el número total de géneros.
   *
   * @return Cantidad total de géneros
   */
  public long count() {
    return generoRepository.count();
  }
}
