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

@Service
public class SerieService {

    private final SerieRepository serieRepository;
    private final GeneroRepository generoRepository;
    private final PlataformaRepository plataformaRepository;
    private final PersonaRepository personaRepository;

    public SerieService(SerieRepository serieRepository,
            GeneroRepository generoRepository,
            PlataformaRepository plataformaRepository,
            PersonaRepository personaRepository) {
        this.serieRepository = serieRepository;
        this.generoRepository = generoRepository;
        this.plataformaRepository = plataformaRepository;
        this.personaRepository = personaRepository;
    }

    public List<Serie> findAll(Sort sort) {
        return serieRepository.findAll(sort);
    }

    public List<Serie> findAll() {
        return serieRepository.findAll(Sort.by(Sort.Direction.DESC, "puntuacion"));
    }

    public Optional<Serie> findById(Long id) {
        return serieRepository.findById(id);
    }

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

        Specification<Serie> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (generoIds != null && !generoIds.isEmpty()) {
                Join<Serie, Genero> generoJoin = root.join("generos");
                predicates.add(generoJoin.get("id").in(generoIds));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (plataformaIds != null && !plataformaIds.isEmpty()) {
                Join<Serie, Plataforma> plataformaJoin = root.join("plataformas");
                predicates.add(plataformaJoin.get("id").in(plataformaIds));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (anioEstrenoMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("anioEstreno"), anioEstrenoMin));
            }

            if (anioEstrenoMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("anioEstreno"), anioEstrenoMax));
            }

            if (puntuacionMin != null && puntuacionMin > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("puntuacion"), puntuacionMin));
            }

            if (nTemporadasMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("nTemporadas"), nTemporadasMin));
            }

            if (nTemporadasMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("nTemporadas"), nTemporadasMax));
            }

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

    @Transactional
    public Serie saveSerieWithRelations(Serie serie, List<Long> generoIds, List<Long> plataformaIds,
            Map<Long, String> personaRoles) {
        updateRelations(serie, generoIds, plataformaIds, personaRoles);
        return serieRepository.save(serie);
    }

    @Transactional
    public Serie updateSerieWithRelations(Long id, Serie serieForm, List<Long> generoIds,
            List<Long> plataformaIds, Map<Long, String> personaRoles) {
        Serie serieExistente = serieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serie no encontrada con ID: " + id));

        serieExistente.setTitulo(serieForm.getTitulo());
        serieExistente.setSinopsis(serieForm.getSinopsis());
        serieExistente.setAnioEstreno(serieForm.getAnioEstreno());
        serieExistente.setPuntuacion(serieForm.getPuntuacion());
        serieExistente.setUrlImagen(serieForm.getUrlImagen());

        serieExistente.setAnioFin(serieForm.getAnioFin());
        serieExistente.setNTemporadas(serieForm.getNTemporadas());

        updateRelations(serieExistente, generoIds, plataformaIds, personaRoles);
        return serieRepository.save(serieExistente);
    }

    private void updateRelations(Serie serie, List<Long> generoIds, List<Long> plataformaIds,
            Map<Long, String> personaRoles) {
        if (generoIds != null) {
            Set<Genero> generos = new HashSet<>(generoRepository.findAllById(generoIds));
            serie.setGeneros(generos);
        } else {
            serie.setGeneros(new HashSet<>());
        }

        if (plataformaIds != null) {
            Set<Plataforma> plataformas = new HashSet<>(plataformaRepository.findAllById(plataformaIds));
            serie.setPlataformas(plataformas);
        } else {
            serie.setPlataformas(new HashSet<>());
        }

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

    @Transactional
    public void deleteById(Long id) {
        serieRepository.deleteById(id);
    }
}
