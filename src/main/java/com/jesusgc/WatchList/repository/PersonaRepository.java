package com.jesusgc.WatchList.repository;

import com.jesusgc.WatchList.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de personas en la base de datos.
 * Proporciona operaciones CRUD para actores, directores y otros profesionales.
 *
 * @author Jesús González Cuenca
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    /**
     * Busca personas por nombre que contenga el texto especificado.
     *
     * @param nombre Texto a buscar en el nombre (ignora mayúsculas/minúsculas)
     * @return Lista de personas que coinciden con la búsqueda
     */
    List<Persona> findByNombreContainingIgnoreCase(String nombre);
}
