package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Plataforma;
import com.jesusgc.WatchList.repository.PlataformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlataformaService {

    private final PlataformaRepository plataformaRepository;

    public PlataformaService(PlataformaRepository plataformaRepository) {
        this.plataformaRepository = plataformaRepository;
    }

    public List<Plataforma> findAll() {
        return plataformaRepository.findAll();
    }

    public Optional<Plataforma> findById(Long id) {
        return plataformaRepository.findById(id);
    }

    @Transactional
    public Plataforma save(Plataforma plataforma) {
        return plataformaRepository.save(plataforma);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!plataformaRepository.existsById(id)) {
            throw new IllegalArgumentException("Plataforma con ID " + id + " no encontrada.");
        }
        plataformaRepository.deleteById(id);
    }

}