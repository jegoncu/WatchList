package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.service.PersonaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Sort;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de personas del mundo del entretenimiento.
 * Maneja la visualización de actores, directores y otros profesionales.
 *
 * @author Jesús González Cuenca
 */
@Controller
public class GenteController {

  private final PersonaService personaService;

  /**
   * Propiedades permitidas para ordenación de personas.
   */
  private static final List<String> ALLOWED_SORT_PROPERTIES_PERSONA = Arrays.asList("nombre", "fechaNac");

  /**
   * Constructor del controlador con inyección de dependencias.
   *
   * @param personaService Servicio de personas
   */
  public GenteController(PersonaService personaService) {
    this.personaService = personaService;
  }

  /**
   * Muestra la lista de personas con ordenación.
   * Soporta tanto navegación normal como peticiones HTMX para actualización
   * parcial.
   *
   * @param model   Modelo para la vista
   * @param sortBy  Campo por el cual ordenar
   * @param sortDir Dirección de ordenación (asc/desc)
   * @param request Solicitud HTTP
   * @return Vista de lista de personas o fragmento HTMX
   */
  @GetMapping("/gente")
  public String mostrarListaGente(Model model,
      @RequestParam(defaultValue = "nombre") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir,
      HttpServletRequest request) {

    // Validar y establecer ordenación
    String sortProperty = ALLOWED_SORT_PROPERTIES_PERSONA.contains(sortBy) ? sortBy : "nombre";
    Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(direction, sortProperty);

    List<Persona> personas = personaService.findAll(sort);
    model.addAttribute("personas", personas);
    model.addAttribute("currentPage", "gente");
    model.addAttribute("currentSortBy", sortProperty);
    model.addAttribute("currentSortDir", direction.name().toLowerCase());
    model.addAttribute("pageTitle", "Listado de Personas");

    // Manejar peticiones HTMX para actualización parcial
    String hxRequestHeader = request.getHeader("HX-Request");
    if (hxRequestHeader != null && hxRequestHeader.equals("true")) {
      return "gente/gente :: #personas-list-container";
    }

    return "gente/gente";
  }

  /**
   * Muestra el detalle de una persona específica con su filmografía.
   *
   * @param id    ID de la persona a mostrar
   * @param model Modelo para la vista
   * @return Vista de detalle de la persona
   * @throws ResponseStatusException si la persona no existe
   */
  @GetMapping("/gente/{id}")
  public String mostrarDetallePersona(@PathVariable Long id, Model model) {
    Optional<Persona> personaOpt = personaService.findById(id);
    if (personaOpt.isPresent()) {
      Persona persona = personaOpt.get();
      model.addAttribute("persona", persona);
      model.addAttribute("currentPage", "gente");
      model.addAttribute("pageTitle", persona.getNombre() + " - WatchList");
      model.addAttribute("hideSidebar", true);
      return "gente/persona-detalle";
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada");
    }
  }
}
