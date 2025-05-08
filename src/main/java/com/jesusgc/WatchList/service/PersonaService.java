package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public List<Persona> findAll() {
        return personaRepository.findAll();
    }

    public Optional<Persona> findById(Long id) {
        return personaRepository.findById(id);
    }

    @Transactional
    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!personaRepository.existsById(id)) {
            throw new IllegalArgumentException("Persona con ID " + id + " no encontrada.");
        }
        personaRepository.deleteById(id);
    }

}