package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.repository.PersonaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de personas del mundo del entretenimiento.
 * Maneja operaciones CRUD para actores, directores y otros profesionales.
 *
 * @author Jesús González Cuenca
 */
@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    /**
     * Constructor del servicio con inyección de dependencias.
     *
     * @param personaRepository Repositorio de personas
     */
    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Obtiene todas las personas del sistema.
     *
     * @return Lista de todas las personas
     */
    public List<Persona> findAll() {
        return personaRepository.findAll();
    }

    /**
     * Obtiene todas las personas ordenadas según el criterio especificado.
     *
     * @param sort Criterio de ordenación
     * @return Lista de personas ordenadas
     */
    public List<Persona> findAll(Sort sort) {
        return personaRepository.findAll(sort);
    }

    /**
     * Busca una persona por su ID.
     *
     * @param id ID de la persona
     * @return Optional conteniendo la persona si existe
     */
    public Optional<Persona> findById(Long id) {
        return personaRepository.findById(id);
    }

    /**
     * Guarda una persona nueva o actualiza una existente.
     *
     * @param persona Persona a guardar
     * @return Persona guardada
     */
    @Transactional
    public Persona save(Persona persona) {
        return personaRepository.save(persona);
    }

    /**
     * Elimina una persona por su ID.
     *
     * @param id ID de la persona a eliminar
     * @throws IllegalArgumentException si la persona no existe
     */
    @Transactional
    public void deleteById(Long id) {
        if (!personaRepository.existsById(id)) {
            throw new IllegalArgumentException("Persona con ID " + id + " no encontrada.");
        }
        personaRepository.deleteById(id);
    }

    /**
     * Busca personas por nombre que contenga el texto especificado.
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de personas que coinciden con la búsqueda
     */
    @Transactional(readOnly = true)
    public List<Persona> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return personaRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }
}
