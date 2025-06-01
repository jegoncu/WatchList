package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Pelicula;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.model.Comentario;
import com.jesusgc.WatchList.service.PeliculaService;
import com.jesusgc.WatchList.service.UsuarioService;
import com.jesusgc.WatchList.service.ListaService;
import com.jesusgc.WatchList.service.ComentarioService;
import com.jesusgc.WatchList.service.GeneroService;
import com.jesusgc.WatchList.service.PlataformaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de películas desde la perspectiva del usuario.
 * Maneja la visualización, búsqueda, filtrado y comentarios de películas.
 *
 * @author Jesús González Cuenca
 */
@Controller
public class PeliculasController {

  private final PeliculaService peliculaService;
  private final UsuarioService usuarioService;
  private final ListaService listaService;
  private final ComentarioService comentarioService;
  private final GeneroService generoService;
  private final PlataformaService plataformaService;

  /**
   * Propiedades permitidas para ordenación de películas.
   */
  private static final List<String> ALLOWED_SORT_PROPERTIES_PELICULA = Arrays.asList("titulo", "anioEstreno",
      "puntuacion", "duracionMin");

  /**
   * Constructor del controlador con inyección de dependencias.
   *
   * @param peliculaService   Servicio de películas
   * @param usuarioService    Servicio de usuarios
   * @param listaService      Servicio de listas
   * @param comentarioService Servicio de comentarios
   * @param generoService     Servicio de géneros
   * @param plataformaService Servicio de plataformas
   */
  public PeliculasController(PeliculaService peliculaService, UsuarioService usuarioService,
      ListaService listaService, ComentarioService comentarioService, GeneroService generoService,
      PlataformaService plataformaService) {
    this.peliculaService = peliculaService;
    this.usuarioService = usuarioService;
    this.listaService = listaService;
    this.comentarioService = comentarioService;
    this.generoService = generoService;
    this.plataformaService = plataformaService;
  }

  /**
   * Muestra la página de películas con filtros y ordenación.
   * Soporta tanto navegación normal como peticiones HTMX para actualización
   * parcial.
   *
   * @param model         Modelo para la vista
   * @param sortBy        Campo por el cual ordenar
   * @param sortDir       Dirección de ordenación (asc/desc)
   * @param generoIds     Lista de IDs de géneros para filtrar
   * @param plataformaIds Lista de IDs de plataformas para filtrar
   * @param anioMin       Año mínimo de estreno
   * @param anioMax       Año máximo de estreno
   * @param puntuacionMin Puntuación mínima
   * @param request       Solicitud HTTP
   * @param response      Respuesta HTTP
   * @param session       Sesión HTTP
   * @return Vista de películas o fragmento HTMX
   */
  @GetMapping("/peliculas")
  public String mostrarPeliculas(Model model,
      @RequestParam(defaultValue = "puntuacion") String sortBy,
      @RequestParam(defaultValue = "desc") String sortDir,
      @RequestParam(required = false) List<Long> generoIds,
      @RequestParam(required = false) List<Long> plataformaIds,
      @RequestParam(required = false) Integer anioMin,
      @RequestParam(required = false) Integer anioMax,
      @RequestParam(required = false) Float puntuacionMin,
      HttpServletRequest request,
      HttpServletResponse response,
      HttpSession session) {

    // Validar y establecer ordenación
    String sortProperty = ALLOWED_SORT_PROPERTIES_PELICULA.contains(sortBy) ? sortBy : "puntuacion";
    Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Sort sort = Sort.by(direction, sortProperty);

    // Establecer valores por defecto para filtros
    Integer currentAnioMin = (anioMin == null) ? 1895 : anioMin;
    Integer currentAnioMax = (anioMax == null) ? Year.now().getValue() : anioMax;
    Float currentPuntuacionMin = (puntuacionMin == null) ? 0.0f : puntuacionMin;

    // Obtener películas filtradas
    List<Pelicula> peliculas = peliculaService.findPeliculasByCriteria(
        generoIds, plataformaIds, currentAnioMin, currentAnioMax, currentPuntuacionMin, sort);

    // Preparar datos para la vista
    model.addAttribute("peliculas", peliculas);
    model.addAttribute("currentPage", "peliculas");
    model.addAttribute("currentSortBy", sortProperty);
    model.addAttribute("currentSortDir", direction.name().toLowerCase());
    model.addAttribute("pageTitle", "Películas");

    // Datos para filtros
    model.addAttribute("allGeneros", generoService.findAll());
    model.addAttribute("allPlataformas", plataformaService.findAll());
    model.addAttribute("selectedGeneroIds", generoIds);
    model.addAttribute("selectedPlataformaIds", plataformaIds);
    model.addAttribute("currentAnioMin", currentAnioMin);
    model.addAttribute("currentAnioMax", currentAnioMax);
    model.addAttribute("currentPuntuacionMin", currentPuntuacionMin);
    model.addAttribute("minAnioVal", 1895);
    model.addAttribute("maxAnioVal", Year.now().getValue());

    // Manejar peticiones HTMX para actualización parcial
    String hxRequestHeader = request.getHeader("HX-Request");
    if (hxRequestHeader != null && hxRequestHeader.equals("true")) {
      response.setHeader("HX-Push-Url", "false");
      return "peliculas/peliculas :: #peliculas-list-container";
    }

    return "peliculas/peliculas";
  }

  /**
   * Muestra el detalle de una película específica con comentarios y listas del
   * usuario.
   *
   * @param id      ID de la película a mostrar
   * @param model   Modelo para la vista
   * @param session Sesión HTTP
   * @return Vista de detalle de la película
   * @throws ResponseStatusException si la película no existe
   */
  @GetMapping("/peliculas/{id}")
  public String mostrarDetallePelicula(@PathVariable("id") Long id, Model model, HttpSession session) {
    Optional<Pelicula> peliculaOptional = peliculaService.findById(id);
    if (peliculaOptional.isPresent()) {
      Pelicula pelicula = peliculaOptional.get();
      model.addAttribute("pelicula", pelicula);
      model.addAttribute("currentPage", "peliculas");
      model.addAttribute("pageTitle", pelicula.getTitulo() + " - WatchList");
      model.addAttribute("hideSidebar", true);

      // Cargar listas del usuario si está autenticado
      Long usuarioId = (Long) session.getAttribute("usuarioId");
      if (usuarioId != null) {
        try {
          Usuario usuarioLogueado = usuarioService.findById(usuarioId);
          if (usuarioLogueado == null) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
          }
          List<Lista> misListas = listaService.obtenerListasPorUsuario(usuarioLogueado);
          model.addAttribute("misListas", misListas);
        } catch (IllegalArgumentException e) {
          model.addAttribute("misListas", Collections.emptyList());
        }
        model.addAttribute("nuevoComentario", new Comentario());
      } else {
        model.addAttribute("misListas", Collections.emptyList());
      }

      // Cargar comentarios de la película
      List<Comentario> comentarios = comentarioService.obtenerComentariosPorMediaId(id);
      model.addAttribute("comentarios", comentarios);

      return "peliculas/pelicula-detalle";
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Película no encontrada");
    }
  }

  /**
   * Procesa la creación de un nuevo comentario en una película.
   *
   * @param peliculaId         ID de la película a comentar
   * @param texto              Texto del comentario
   * @param puntuacion         Puntuación del 1 al 5
   * @param session            Sesión HTTP
   * @param redirectAttributes Atributos para redirección
   * @return Redirección al detalle de la película
   */
  @PostMapping("/peliculas/{peliculaId}/comentar")
  public String guardarComentarioPelicula(@PathVariable Long peliculaId,
      @RequestParam String texto,
      @RequestParam int puntuacion,
      HttpSession session,
      RedirectAttributes redirectAttributes) {

    // Verificar autenticación
    Long usuarioId = (Long) session.getAttribute("usuarioId");
    if (usuarioId == null) {
      redirectAttributes.addFlashAttribute("errorGeneral", "Debes iniciar sesión para comentar.");
      return "redirect:/login";
    }

    // Verificar que la película existe
    Optional<Pelicula> peliculaOpt = peliculaService.findById(peliculaId);
    if (peliculaOpt.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorGeneral", "Película no encontrada.");
      return "redirect:/peliculas";
    }
    Pelicula pelicula = peliculaOpt.get();

    try {
      // Validaciones del comentario
      if (texto == null || texto.trim().isEmpty()) {
        redirectAttributes.addFlashAttribute("errorComentario",
            "El texto del comentario no puede estar vacío.");
        redirectAttributes.addFlashAttribute("textoPrevio", texto);
        redirectAttributes.addFlashAttribute("puntuacionPrevia", puntuacion);
        return "redirect:/peliculas/" + peliculaId;
      }
      if (puntuacion < 1 || puntuacion > 5) {
        redirectAttributes.addFlashAttribute("errorComentario", "La puntuación debe estar entre 1 y 5.");
        redirectAttributes.addFlashAttribute("textoPrevio", texto);
        redirectAttributes.addFlashAttribute("puntuacionPrevia", puntuacion);
        return "redirect:/peliculas/" + peliculaId;
      }

      comentarioService.guardarComentario(pelicula, texto, puntuacion, usuarioId);
      redirectAttributes.addFlashAttribute("exitoComentario", "Comentario añadido correctamente.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("errorComentario",
          "Error al guardar el comentario: " + e.getMessage());
      redirectAttributes.addFlashAttribute("textoPrevio", texto);
      redirectAttributes.addFlashAttribute("puntuacionPrevia", puntuacion);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorComentario",
          "Error inesperado al guardar el comentario. Inténtalo de nuevo más tarde.");
      redirectAttributes.addFlashAttribute("textoPrevio", texto);
      redirectAttributes.addFlashAttribute("puntuacionPrevia", puntuacion);
    }

    return "redirect:/peliculas/" + peliculaId;
  }

  /**
   * Elimina un comentario de una película si pertenece al usuario autenticado.
   *
   * @param peliculaId         ID de la película
   * @param comentarioId       ID del comentario a eliminar
   * @param session            Sesión HTTP
   * @param redirectAttributes Atributos para redirección
   * @return Redirección al detalle de la película
   */
  @PostMapping("/peliculas/{id}/comentarios/{comentarioId}/eliminar")
  public String eliminarComentarioPelicula(
      @PathVariable("id") Long peliculaId,
      @PathVariable("comentarioId") Long comentarioId,
      HttpSession session,
      RedirectAttributes redirectAttributes) {

    // Verificar autenticación
    Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuario == null) {
      redirectAttributes.addFlashAttribute("errorMessage", "Debes estar logueado para eliminar comentarios.");
      return "redirect:/peliculas/" + peliculaId;
    }

    try {
      comentarioService.eliminarComentario(comentarioId, usuario);
      redirectAttributes.addFlashAttribute("successMessage", "Comentario eliminado correctamente.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el comentario: " + e.getMessage());
    }

    return "redirect:/peliculas/" + peliculaId;
  }
}
