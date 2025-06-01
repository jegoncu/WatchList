package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Pelicula;
import com.jesusgc.WatchList.model.Genero;
import com.jesusgc.WatchList.model.Plataforma;
import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.model.Credito;
import com.jesusgc.WatchList.repository.PeliculaRepository;
import com.jesusgc.WatchList.repository.GeneroRepository;
import com.jesusgc.WatchList.repository.PlataformaRepository;
import com.jesusgc.WatchList.repository.PersonaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para la gestión de películas en el sistema.
 * Maneja operaciones CRUD y consultas avanzadas con filtros dinámicos.
 *
 * @author Jesús González Cuenca
 */
@Service
public class PeliculaService {

  private final PeliculaRepository peliculaRepository;
  private final GeneroRepository generoRepository;
  private final PlataformaRepository plataformaRepository;
  private final PersonaRepository personaRepository;

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Constructor del servicio con inyección de dependencias.
   *
   * @param peliculaRepository   Repositorio de películas
   * @param generoRepository     Repositorio de géneros
   * @param plataformaRepository Repositorio de plataformas
   * @param personaRepository    Repositorio de personas
   */
  public PeliculaService(PeliculaRepository peliculaRepository,
      GeneroRepository generoRepository,
      PlataformaRepository plataformaRepository,
      PersonaRepository personaRepository) {
    this.peliculaRepository = peliculaRepository;
    this.generoRepository = generoRepository;
    this.plataformaRepository = plataformaRepository;
    this.personaRepository = personaRepository;
  }

  /**
   * Obtiene todas las películas del sistema.
   *
   * @return Lista de todas las películas
   */
  public List<Pelicula> findAll() {
    return peliculaRepository.findAll();
  }

  /**
   * Obtiene todas las películas ordenadas según el criterio especificado.
   *
   * @param sort Criterio de ordenación
   * @return Lista de películas ordenadas
   */
  public List<Pelicula> findAll(Sort sort) {
    return peliculaRepository.findAll(sort);
  }

  /**
   * Busca películas aplicando filtros dinámicos y ordenación.
   *
   * @param generoIds     Lista de IDs de géneros a filtrar
   * @param plataformaIds Lista de IDs de plataformas a filtrar
   * @param anioMin       Año mínimo de estreno
   * @param anioMax       Año máximo de estreno
   * @param puntuacionMin Puntuación mínima
   * @param sort          Criterio de ordenación
   * @return Lista de películas que cumplen los criterios
   */
  @Transactional(readOnly = true)
  public List<Pelicula> findPeliculasByCriteria(
      List<Long> generoIds,
      List<Long> plataformaIds,
      Integer anioMin,
      Integer anioMax,
      Float puntuacionMin,
      Sort sort) {

    // Especificación dinámica para construir consultas con criterios variables
    Specification<Pelicula> spec = (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Filtro por géneros (OR entre géneros seleccionados)
      if (generoIds != null && !generoIds.isEmpty()) {
        Join<Pelicula, Genero> generoJoin = root.join("generos");
        predicates.add(generoJoin.get("id").in(generoIds));
        if (query != null) {
          query.distinct(true); // Evitar duplicados por los joins
        }
      }

      // Filtro por plataformas (OR entre plataformas seleccionadas)
      if (plataformaIds != null && !plataformaIds.isEmpty()) {
        Join<Pelicula, Plataforma> plataformaJoin = root.join("plataformas");
        predicates.add(plataformaJoin.get("id").in(plataformaIds));
        if (query != null) {
          query.distinct(true);
        }
      }

      // Filtro por rango de años de estreno
      if (anioMin != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("anioEstreno"), anioMin));
      }

      if (anioMax != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("anioEstreno"), anioMax));
      }

      // Filtro por puntuación mínima
      if (puntuacionMin != null && puntuacionMin > 0) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("puntuacion"), puntuacionMin));
      }

      if (predicates.isEmpty()) {
        return null;
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };

    // Si no hay filtros activos, devolver todas las películas ordenadas
    boolean hasActiveFilters = (generoIds != null && !generoIds.isEmpty()) ||
        (plataformaIds != null && !plataformaIds.isEmpty()) ||
        (anioMin != null) ||
        (anioMax != null) ||
        (puntuacionMin != null && puntuacionMin > 0);

    if (!hasActiveFilters) {
      return peliculaRepository.findAll(sort);
    }

    return peliculaRepository.findAll(spec, sort);
  }

  /**
   * Busca una película por su ID.
   *
   * @param id ID de la película
   * @return Optional conteniendo la película si existe
   */
  public Optional<Pelicula> findById(Long id) {
    return peliculaRepository.findById(id);
  }

  /**
   * Guarda una nueva película con sus relaciones (géneros, plataformas,
   * créditos).
   *
   * @param pelicula      Película a guardar
   * @param generoIds     Lista de IDs de géneros asociados
   * @param plataformaIds Lista de IDs de plataformas asociadas
   * @param personaRoles  Mapa de persona ID -> rol para los créditos
   * @return Película guardada con todas sus relaciones
   */
  @Transactional
  public Pelicula savePeliculaWithRelations(Pelicula pelicula, List<Long> generoIds, List<Long> plataformaIds,
      Map<Long, String> personaRoles) {
    updateRelations(pelicula, generoIds, plataformaIds, personaRoles);
    return peliculaRepository.save(pelicula);
  }

  /**
   * Actualiza una película existente con nuevos datos y relaciones.
   *
   * @param id            ID de la película a actualizar
   * @param peliculaForm  Datos actualizados de la película
   * @param generoIds     Lista de IDs de géneros asociados
   * @param plataformaIds Lista de IDs de plataformas asociadas
   * @param personaRoles  Mapa de persona ID -> rol para los créditos
   * @return Película actualizada
   * @throws IllegalArgumentException si la película no existe
   */
  @Transactional
  public Pelicula updatePeliculaWithRelations(Long id, Pelicula peliculaForm, List<Long> generoIds,
      List<Long> plataformaIds, Map<Long, String> personaRoles) {

    Pelicula peliculaExistente = peliculaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Película no encontrada con ID: " + id));

    // Actualizar campos básicos
    peliculaExistente.setTitulo(peliculaForm.getTitulo());
    peliculaExistente.setSinopsis(peliculaForm.getSinopsis());
    peliculaExistente.setAnioEstreno(peliculaForm.getAnioEstreno());
    peliculaExistente.setPuntuacion(peliculaForm.getPuntuacion());
    peliculaExistente.setUrlTrailer(peliculaForm.getUrlTrailer());
    peliculaExistente.setUrlImagen(peliculaForm.getUrlImagen());
    peliculaExistente.setDuracionMin(peliculaForm.getDuracionMin());

    updateRelations(peliculaExistente, generoIds, plataformaIds, personaRoles);

    return peliculaRepository.save(peliculaExistente);
  }

  /**
   * Actualiza las relaciones de una película con géneros, plataformas y créditos.
   *
   * @param pelicula      Película a actualizar
   * @param generoIds     Lista de IDs de géneros
   * @param plataformaIds Lista de IDs de plataformas
   * @param personaRoles  Mapa de persona ID -> rol
   */
  private void updateRelations(Pelicula pelicula, List<Long> generoIds, List<Long> plataformaIds,
      Map<Long, String> personaRoles) {
    // Actualizar géneros
    if (generoIds != null) {
      Set<Genero> generos = new HashSet<>(generoRepository.findAllById(generoIds));
      pelicula.setGeneros(generos);
    } else {
      pelicula.setGeneros(new HashSet<>());
    }

    // Actualizar plataformas
    if (plataformaIds != null) {
      Set<Plataforma> plataformas = new HashSet<>(plataformaRepository.findAllById(plataformaIds));
      pelicula.setPlataformas(plataformas);
    } else {
      pelicula.setPlataformas(new HashSet<>());
    }

    // Limpiar créditos existentes y añadir los nuevos
    pelicula.getCreditos().clear();

    if (personaRoles != null) {
      for (Map.Entry<Long, String> entry : personaRoles.entrySet()) {
        Long personaId = entry.getKey();
        String rol = entry.getValue();

        Persona persona = personaRepository.findById(personaId)
            .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con ID: " + personaId));

        Credito novoCredito = new Credito(pelicula, persona, rol);
        pelicula.getCreditos().add(novoCredito);
      }
    }
  }

  /**
   * Elimina una película por su ID.
   *
   * @param id ID de la película a eliminar
   */
  @Transactional
  public void deleteById(Long id) {
    peliculaRepository.deleteById(id);
  }

  /**
   * Busca películas por título que contenga el texto especificado.
   *
   * @param titulo Texto a buscar en el título
   * @return Lista de películas que coinciden con la búsqueda
   */
  @Transactional(readOnly = true)
  public List<Pelicula> buscarPorTitulo(String titulo) {
    if (titulo == null || titulo.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return peliculaRepository.findByTituloContainingIgnoreCase(titulo.trim());
  }
}
