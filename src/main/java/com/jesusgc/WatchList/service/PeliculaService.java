package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Pelicula;
import com.jesusgc.WatchList.model.Genero;
import com.jesusgc.WatchList.model.Plataforma;
import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.repository.PeliculaRepository;
import com.jesusgc.WatchList.repository.GeneroRepository;
import com.jesusgc.WatchList.repository.PlataformaRepository;
import com.jesusgc.WatchList.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;
    private final PlataformaRepository plataformaRepository;
    private final PersonaRepository personaRepository;

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

    public Optional<Pelicula> findById(Long id) {
        return peliculaRepository.findById(id);
    }

    @Transactional
    public Pelicula savePeliculaWithRelations(Pelicula pelicula, List<Long> generoIds, List<Long> plataformaIds,
            List<Long> personaIds) {
        updateRelations(pelicula, generoIds, plataformaIds, personaIds);
        return peliculaRepository.save(pelicula);
    }

    @Transactional
    public Pelicula updatePeliculaWithRelations(Long id, Pelicula peliculaForm, List<Long> generoIds,
            List<Long> plataformaIds, List<Long> personaIds) {
        Pelicula peliculaExistente = peliculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada con ID: " + id));

        peliculaExistente.setTitulo(peliculaForm.getTitulo());
        peliculaExistente.setSinopsis(peliculaForm.getSinopsis());
        peliculaExistente.setAnioEstreno(peliculaForm.getAnioEstreno());
        peliculaExistente.setPuntuacion(peliculaForm.getPuntuacion());
        peliculaExistente.setUrlTrailer(peliculaForm.getUrlTrailer());
        peliculaExistente.setUrlImagen(peliculaForm.getUrlImagen());

        peliculaExistente.setDuracionMin(peliculaForm.getDuracionMin());

        updateRelations(peliculaExistente, generoIds, plataformaIds, personaIds);
        return peliculaRepository.save(peliculaExistente);
    }

    private void updateRelations(Pelicula pelicula, List<Long> generoIds, List<Long> plataformaIds,
            List<Long> personaIds) {
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

        if (personaIds != null) {
            Set<Persona> personas = new HashSet<>(personaRepository.findAllById(personaIds));
            pelicula.setPersonas(personas);
        } else {
            pelicula.setPersonas(new HashSet<>());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        peliculaRepository.deleteById(id);
    }
}