package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Serie;
import com.jesusgc.WatchList.model.Genero;
import com.jesusgc.WatchList.model.Plataforma;
import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.model.Credito;
import com.jesusgc.WatchList.repository.SerieRepository;
import com.jesusgc.WatchList.repository.GeneroRepository;
import com.jesusgc.WatchList.repository.PlataformaRepository;
import com.jesusgc.WatchList.repository.PersonaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Servicio para la gestión de series de televisión.
 * Maneja operaciones CRUD y consultas avanzadas con filtros dinámicos.
 *
 * @author Jesús González Cuenca
 */
@Service
public class SerieService {

  private final SerieRepository serieRepository;
  private final GeneroRepository generoRepository;
  private final PlataformaRepository plataformaRepository;
  private final PersonaRepository personaRepository;

  /**
   * Constructor del servicio con inyección de dependencias.
   *
   * @param serieRepository      Repositorio de series
   * @param generoRepository     Repositorio de géneros
   * @param plataformaRepository Repositorio de plataformas
   * @param personaRepository    Repositorio de personas
   */
  public SerieService(SerieRepository serieRepository,
      GeneroRepository generoRepository,
      PlataformaRepository plataformaRepository,
      PersonaRepository personaRepository) {
    this.serieRepository = serieRepository;
    this.generoRepository = generoRepository;
    this.plataformaRepository = plataformaRepository;
    this.personaRepository = personaRepository;
  }

  /**
   * Obtiene todas las series ordenadas según el criterio especificado.
   *
   * @param sort Criterio de ordenación
   * @return Lista de series ordenadas
   */
  public List<Serie> findAll(Sort sort) {
    return serieRepository.findAll(sort);
  }

  /**
   * Obtiene todas las series ordenadas por puntuación descendente.
   *
   * @return Lista de series ordenadas por puntuación
   */
  public List<Serie> findAll() {
    return serieRepository.findAll(Sort.by(Sort.Direction.DESC, "puntuacion"));
  }

  /**
   * Busca una serie por su ID.
   *
   * @param id ID de la serie
   * @return Optional conteniendo la serie si existe
   */
  public Optional<Serie> findById(Long id) {
    return serieRepository.findById(id);
  }

  /**
   * Busca series aplicando filtros dinámicos y ordenación.
   *
   * @param generoIds      Lista de IDs de géneros a filtrar
   * @param plataformaIds  Lista de IDs de plataformas a filtrar
   * @param anioEstrenoMin Año mínimo de estreno
   * @param anioEstrenoMax Año máximo de estreno
   * @param puntuacionMin  Puntuación mínima
   * @param nTemporadasMin Número mínimo de temporadas
   * @param nTemporadasMax Número máximo de temporadas
   * @param estadoSerie    Estado de la serie (enEmision/finalizada/todos)
   * @param sort           Criterio de ordenación
   * @return Lista de series que cumplen los criterios
   */
  @Transactional(readOnly = true)
  public List<Serie> findSeriesByCriteria(
      List<Long> generoIds,
      List<Long> plataformaIds,
      Integer anioEstrenoMin,
      Integer anioEstrenoMax,
      Float puntuacionMin,
      Integer nTemporadasMin,
      Integer nTemporadasMax,
      String estadoSerie,
      Sort sort) {

    // Especificación dinámica para construir consultas con criterios variables
    Specification<Serie> spec = (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Filtro por géneros (OR entre géneros seleccionados)
      if (generoIds != null && !generoIds.isEmpty()) {
        Join<Serie, Genero> generoJoin = root.join("generos");
        predicates.add(generoJoin.get("id").in(generoIds));
        if (query != null) {
          query.distinct(true); // Evitar duplicados por los joins
        }
      }

      // Filtro por plataformas (OR entre plataformas seleccionadas)
      if (plataformaIds != null && !plataformaIds.isEmpty()) {
        Join<Serie, Plataforma> plataformaJoin = root.join("plataformas");
        predicates.add(plataformaJoin.get("id").in(plataformaIds));
        if (query != null) {
          query.distinct(true);
        }
      }

      // Filtro por rango de años de estreno
      if (anioEstrenoMin != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("anioEstreno"), anioEstrenoMin));
      }

      if (anioEstrenoMax != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("anioEstreno"), anioEstrenoMax));
      }

      // Filtro por puntuación mínima
      if (puntuacionMin != null && puntuacionMin > 0) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("puntuacion"), puntuacionMin));
      }

      // Filtro por rango de número de temporadas
      if (nTemporadasMin != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("nTemporadas"), nTemporadasMin));
      }

      if (nTemporadasMax != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("nTemporadas"), nTemporadasMax));
      }

      // Filtro por estado de la serie (en emisión o finalizada)
      if (estadoSerie != null && !estadoSerie.equalsIgnoreCase("todos")) {
        if (estadoSerie.equalsIgnoreCase("enEmision")) {
          predicates.add(criteriaBuilder.isNull(root.get("anioFin")));
        } else if (estadoSerie.equalsIgnoreCase("finalizada")) {
          predicates.add(criteriaBuilder.isNotNull(root.get("anioFin")));
        }
      }

      if (predicates.isEmpty()) {
        return null;
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };

    // Si no hay filtros activos, devolver todas las series ordenadas
    boolean hasActiveFilters = (generoIds != null && !generoIds.isEmpty()) ||
        (plataformaIds != null && !plataformaIds.isEmpty()) ||
        (anioEstrenoMin != null) ||
        (anioEstrenoMax != null) ||
        (puntuacionMin != null && puntuacionMin > 0) ||
        (nTemporadasMin != null) ||
        (nTemporadasMax != null) ||
        (estadoSerie != null && !estadoSerie.equalsIgnoreCase("todos"));

    if (!hasActiveFilters) {
      return serieRepository.findAll(sort);
    }
    return serieRepository.findAll(spec, sort);
  }

  /**
   * Guarda una nueva serie con sus relaciones (géneros, plataformas, créditos).
   *
   * @param serie         Serie a guardar
   * @param generoIds     Lista de IDs de géneros asociados
   * @param plataformaIds Lista de IDs de plataformas asociadas
   * @param personaRoles  Mapa de persona ID -> rol para los créditos
   * @return Serie guardada con todas sus relaciones
   */
  @Transactional
  public Serie saveSerieWithRelations(Serie serie, List<Long> generoIds, List<Long> plataformaIds,
      Map<Long, String> personaRoles) {
    updateRelations(serie, generoIds, plataformaIds, personaRoles);
    return serieRepository.save(serie);
  }

  /**
   * Actualiza una serie existente con nuevos datos y relaciones.
   *
   * @param id            ID de la serie a actualizar
   * @param serieForm     Datos actualizados de la serie
   * @param generoIds     Lista de IDs de géneros asociados
   * @param plataformaIds Lista de IDs de plataformas asociadas
   * @param personaRoles  Mapa de persona ID -> rol para los créditos
   * @return Serie actualizada
   * @throws IllegalArgumentException si la serie no existe
   */
  @Transactional
  public Serie updateSerieWithRelations(Long id, Serie serieForm, List<Long> generoIds,
      List<Long> plataformaIds, Map<Long, String> personaRoles) {
    Serie serieExistente = serieRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Serie no encontrada con ID: " + id));

    // Actualizar campos básicos
    serieExistente.setTitulo(serieForm.getTitulo());
    serieExistente.setSinopsis(serieForm.getSinopsis());
    serieExistente.setAnioEstreno(serieForm.getAnioEstreno());
    serieExistente.setPuntuacion(serieForm.getPuntuacion());
    serieExistente.setUrlImagen(serieForm.getUrlImagen());

    // Actualizar campos específicos de serie
    serieExistente.setAnioFin(serieForm.getAnioFin());
    serieExistente.setNTemporadas(serieForm.getNTemporadas());

    updateRelations(serieExistente, generoIds, plataformaIds, personaRoles);
    return serieRepository.save(serieExistente);
  }

  /**
   * Actualiza las relaciones de una serie con géneros, plataformas y créditos.
   *
   * @param serie         Serie a actualizar
   * @param generoIds     Lista de IDs de géneros
   * @param plataformaIds Lista de IDs de plataformas
   * @param personaRoles  Mapa de persona ID -> rol
   */
  private void updateRelations(Serie serie, List<Long> generoIds, List<Long> plataformaIds,
      Map<Long, String> personaRoles) {
    // Actualizar géneros
    if (generoIds != null) {
      Set<Genero> generos = new HashSet<>(generoRepository.findAllById(generoIds));
      serie.setGeneros(generos);
    } else {
      serie.setGeneros(new HashSet<>());
    }

    // Actualizar plataformas
    if (plataformaIds != null) {
      Set<Plataforma> plataformas = new HashSet<>(plataformaRepository.findAllById(plataformaIds));
      serie.setPlataformas(plataformas);
    } else {
      serie.setPlataformas(new HashSet<>());
    }

    // Limpiar créditos existentes y añadir los nuevos
    serie.getCreditos().clear();

    if (personaRoles != null) {
      for (Map.Entry<Long, String> entry : personaRoles.entrySet()) {
        Long personaId = entry.getKey();
        String rol = entry.getValue();

        Persona persona = personaRepository.findById(personaId)
            .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con ID: " + personaId));

        Credito nuevoCredito = new Credito(serie, persona, rol);
        serie.getCreditos().add(nuevoCredito);
      }
    }
  }

  /**
   * Elimina una serie por su ID.
   *
   * @param id ID de la serie a eliminar
   */
  @Transactional
  public void deleteById(Long id) {
    serieRepository.deleteById(id);
  }

  /**
   * Busca series por título que contenga el texto especificado.
   *
   * @param titulo Texto a buscar en el título
   * @return Lista de series que coinciden con la búsqueda
   */
  @Transactional(readOnly = true)
  public List<Serie> buscarPorTitulo(String titulo) {
    if (titulo == null || titulo.trim().isEmpty()) {
      return new ArrayList<>();
    }
    return serieRepository.findByTituloContainingIgnoreCase(titulo.trim());
  }
}
