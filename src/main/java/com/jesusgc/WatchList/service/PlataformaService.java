package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Plataforma;
import com.jesusgc.WatchList.repository.PlataformaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de plataformas de streaming y distribución.
 * Proporciona operaciones CRUD para las plataformas del sistema.
 *
 * @author Jesús González Cuenca
 */
@Service
public class PlataformaService {

    private final PlataformaRepository plataformaRepository;

    /**
     * Constructor del servicio con inyección de dependencias.
     *
     * @param plataformaRepository Repositorio de plataformas
     */
    public PlataformaService(PlataformaRepository plataformaRepository) {
        this.plataformaRepository = plataformaRepository;
    }

    /**
     * Obtiene todas las plataformas disponibles.
     *
     * @return Lista de todas las plataformas
     */
    public List<Plataforma> findAll() {
        return plataformaRepository.findAll();
    }

    /**
     * Busca una plataforma por su ID.
     *
     * @param id ID de la plataforma
     * @return Optional conteniendo la plataforma si existe
     */
    public Optional<Plataforma> findById(Long id) {
        return plataformaRepository.findById(id);
    }

    /**
     * Guarda una plataforma nueva o actualiza una existente.
     *
     * @param plataforma Plataforma a guardar
     * @return Plataforma guardada
     */
    @Transactional
    public Plataforma save(Plataforma plataforma) {
        return plataformaRepository.save(plataforma);
    }

    /**
     * Elimina una plataforma por su ID.
     *
     * @param id ID de la plataforma a eliminar
     * @throws IllegalArgumentException si la plataforma no existe
     */
    @Transactional
    public void deleteById(Long id) {
        if (!plataformaRepository.existsById(id)) {
            throw new IllegalArgumentException("Plataforma con ID " + id + " no encontrada.");
        }
        plataformaRepository.deleteById(id);
    }
}
