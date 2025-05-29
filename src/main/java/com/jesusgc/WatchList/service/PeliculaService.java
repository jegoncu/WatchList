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

@Service
public class PeliculaService {
    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;
    private final PlataformaRepository plataformaRepository;
    private final PersonaRepository personaRepository;

    @PersistenceContext
    private EntityManager entityManager;
    public PeliculaService(PeliculaRepository peliculaRepository,
                           GeneroRepository generoRepository,
                           PlataformaRepository plataformaRepository,
                           PersonaRepository personaRepository) {
        this.peliculaRepository = peliculaRepository;
        this.generoRepository = generoRepository;
        this.plataformaRepository = plataformaRepository;
        this.personaRepository = personaRepository;
    }

    public List<Pelicula> findAll() {
        return peliculaRepository.findAll();
    }

    public List<Pelicula> findAll(Sort sort) {
        return peliculaRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<Pelicula> findPeliculasByCriteria(
            List<Long> generoIds,
            List<Long> plataformaIds,
            Integer anioMin,
            Integer anioMax,
            Float puntuacionMin,
            Sort sort) {

        Specification<Pelicula> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (generoIds != null && !generoIds.isEmpty()) {
                Join<Pelicula, Genero> generoJoin = root.join("generos");
                predicates.add(generoJoin.get("id").in(generoIds));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (plataformaIds != null && !plataformaIds.isEmpty()) {
                Join<Pelicula, Plataforma> plataformaJoin = root.join("plataformas");
                predicates.add(plataformaJoin.get("id").in(plataformaIds));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (anioMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("anioEstreno"), anioMin));
            }

            if (anioMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("anioEstreno"), anioMax));
            }

            if (puntuacionMin != null && puntuacionMin > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("puntuacion"), puntuacionMin));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

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

    public Optional<Pelicula> findById(Long id) {
        return peliculaRepository.findById(id);
    }

    @Transactional
    public Pelicula savePeliculaWithRelations(Pelicula pelicula, List<Long> generoIds, List<Long> plataformaIds,
                                              Map<Long, String> personaRoles) {
        updateRelations(pelicula, generoIds, plataformaIds, personaRoles);
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public Pelicula updatePeliculaWithRelations(Long id, Pelicula peliculaForm, List<Long> generoIds,
                                        List<Long> plataformaIds, Map<Long, String> personaRoles) {

    Pelicula peliculaExistente = peliculaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Película no encontrada con ID: " + id));

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

    private void updateRelations(Pelicula pelicula, List<Long> generoIds, List<Long> plataformaIds,
                                 Map<Long, String> personaRoles) {
        if (generoIds != null) {
            Set<Genero> generos = new HashSet<>(generoRepository.findAllById(generoIds));
            pelicula.setGeneros(generos);
        } else {
            pelicula.setGeneros(new HashSet<>());
        }

        if (plataformaIds != null) {
            Set<Plataforma> plataformas = new HashSet<>(plataformaRepository.findAllById(plataformaIds));
            pelicula.setPlataformas(plataformas);
        } else {
            pelicula.setPlataformas(new HashSet<>());
        }

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

    @Transactional
    public void deleteById(Long id) {
        peliculaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Pelicula> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return peliculaRepository.findByTituloContainingIgnoreCase(titulo.trim());
    }
}
