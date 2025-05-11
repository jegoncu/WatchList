package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Genero;
import com.jesusgc.WatchList.repository.GeneroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GeneroService {

    private final GeneroRepository generoRepository;

    public GeneroService(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    public List<Genero> findAll() {
        return generoRepository.findAll();
    }

    public Optional<Genero> findById(Long id) {
        return generoRepository.findById(id);
    }

    @Transactional
    public Genero save(Genero genero) {
        return generoRepository.save(genero);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!generoRepository.existsById(id)) {
            throw new IllegalArgumentException("Género con ID " + id + " no encontrado.");
        }
        generoRepository.deleteById(id);
    }

    public long count() {
        return generoRepository.count();
    }
}