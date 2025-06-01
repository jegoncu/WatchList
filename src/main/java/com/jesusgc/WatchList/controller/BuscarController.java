package com.jesusgc.WatchList.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jesusgc.WatchList.service.PeliculaService;
import com.jesusgc.WatchList.service.SerieService;
import com.jesusgc.WatchList.service.PersonaService;
import com.jesusgc.WatchList.service.ListaService;

import java.util.List;

/**
 * Controlador para funcionalidades de búsqueda global en el sistema.
 * Permite buscar simultáneamente en películas, series, personas y listas.
 *
 * @author Jesús González Cuenca
 */
@Controller
public class BuscarController {

  private final PeliculaService peliculaService;
  private final SerieService serieService;
  private final PersonaService personaService;
  private final ListaService listaService;

  /**
   * Constructor del controlador con inyección de dependencias.
   *
   * @param peliculaService Servicio de películas
   * @param serieService    Servicio de series
   * @param personaService  Servicio de personas
   * @param listaService    Servicio de listas
   */
  public BuscarController(PeliculaService peliculaService, SerieService serieService,
      PersonaService personaService, ListaService listaService) {
    this.peliculaService = peliculaService;
    this.serieService = serieService;
    this.personaService = personaService;
    this.listaService = listaService;
  }

  /**
   * Realiza una búsqueda global en todas las entidades del sistema.
   * Busca coincidencias en películas, series, personas y listas públicas.
   *
   * @param query Término de búsqueda ingresado por el usuario
   * @param model Modelo para la vista
   * @return Vista de resultados de búsqueda o redirección si la consulta está
   *         vacía
   */
  @GetMapping("/buscar")
  public String buscarTodo(@RequestParam("q") String query, Model model) {
    // Validar que la consulta no esté vacía
    if (query == null || query.trim().isEmpty()) {
      return "redirect:/";
    }

    String queryLimpio = query.trim();

    // Realizar búsquedas en paralelo en todas las entidades
    List<?> peliculas = peliculaService.buscarPorTitulo(queryLimpio);
    List<?> series = serieService.buscarPorTitulo(queryLimpio);
    List<?> personas = personaService.buscarPorNombre(queryLimpio);
    List<?> listas = listaService.buscarPorTitulo(queryLimpio);

    // Preparar datos para la vista de resultados
    model.addAttribute("query", queryLimpio);
    model.addAttribute("peliculas", peliculas);
    model.addAttribute("series", series);
    model.addAttribute("personas", personas);
    model.addAttribute("listas", listas);

    model.addAttribute("currentPage", "buscar");
    model.addAttribute("hideSidebar", true);

    return "buscar/resultados";
  }
}
