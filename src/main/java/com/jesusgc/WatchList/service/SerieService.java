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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

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

    public List<Serie> findAll() {
        return serieRepository.findAll();
    }

    public Optional<Serie> findById(Long id) {
        return serieRepository.findById(id);
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
        // Ya no necesitamos setear urlTrailer en Serie
        // serieExistente.setUrlTrailer(serieForm.getUrlTrailer());
        serieExistente.setUrlImagen(serieForm.getUrlImagen());

        // Campos específicos de serie
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

                Persona persona = personaRepository.findById(personaId).orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con ID: " + personaId));

                Credito novoCredito = new Credito(serie, persona, rol);
                serie.getCreditos().add(novoCredito);
            }
        }
    }

    @Transactional
    public void deleteById(Long id) {
        serieRepository.deleteById(id);
    }
}
