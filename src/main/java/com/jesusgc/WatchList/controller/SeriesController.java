package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Serie;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.model.Comentario;
import com.jesusgc.WatchList.service.SerieService;
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

@Controller
public class SeriesController {

    private final SerieService serieService;
    private final UsuarioService usuarioService;
    private final ListaService listaService;
    private final ComentarioService comentarioService;
    private final GeneroService generoService;
    private final PlataformaService plataformaService;

    private static final List<String> ALLOWED_SORT_PROPERTIES_SERIE = Arrays.asList("titulo", "anioEstreno",
            "puntuacion", "nTemporadas");

    public SeriesController(SerieService serieService, UsuarioService usuarioService,
            ListaService listaService, ComentarioService comentarioService,
            GeneroService generoService, PlataformaService plataformaService) {
        this.serieService = serieService;
        this.usuarioService = usuarioService;
        this.listaService = listaService;
        this.comentarioService = comentarioService;
        this.generoService = generoService;
        this.plataformaService = plataformaService;
    }

    @GetMapping("/series")
    public String mostrarSeries(Model model,
            @RequestParam(defaultValue = "puntuacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) List<Long> generoIds,
            @RequestParam(required = false) List<Long> plataformaIds,
            @RequestParam(required = false) Integer anioEstrenoMin,
            @RequestParam(required = false) Integer anioEstrenoMax,
            @RequestParam(required = false) Float puntuacionMin,
            @RequestParam(required = false) Integer nTemporadasMin,
            @RequestParam(required = false) Integer nTemporadasMax,
            @RequestParam(required = false, defaultValue = "todos") String estadoSerie,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session) {

        String sortProperty = ALLOWED_SORT_PROPERTIES_SERIE.contains(sortBy) ? sortBy : "puntuacion";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortProperty);
        Integer currentAnioEstrenoMin = (anioEstrenoMin == null) ? 1950 : anioEstrenoMin;
        Integer currentAnioEstrenoMax = (anioEstrenoMax == null) ? Year.now().getValue() : anioEstrenoMax;
        Float currentPuntuacionMin = (puntuacionMin == null) ? 0.0f : puntuacionMin;
        Integer currentNTemporadasMin = (nTemporadasMin == null) ? 1 : nTemporadasMin;
        Integer currentNTemporadasMax = nTemporadasMax;
        String currentEstadoSerie = (estadoSerie == null || estadoSerie.trim().isEmpty()) ? "todos" : estadoSerie;
        List<Serie> series = serieService.findSeriesByCriteria(
                generoIds, plataformaIds, currentAnioEstrenoMin, currentAnioEstrenoMax,
                currentPuntuacionMin, currentNTemporadasMin, currentNTemporadasMax, currentEstadoSerie, sort);

        model.addAttribute("series", series);
        model.addAttribute("currentPage", "series");
        model.addAttribute("currentSortBy", sortProperty);
        model.addAttribute("currentSortDir", direction.name().toLowerCase());
        model.addAttribute("pageTitle", "Series");

        model.addAttribute("allGeneros", generoService.findAll());
        model.addAttribute("allPlataformas", plataformaService.findAll());
        model.addAttribute("selectedGeneroIds", generoIds != null ? generoIds : Collections.emptyList());
        model.addAttribute("selectedPlataformaIds", plataformaIds != null ? plataformaIds : Collections.emptyList());

        model.addAttribute("currentAnioEstrenoMin", currentAnioEstrenoMin);
        model.addAttribute("currentAnioEstrenoMax", currentAnioEstrenoMax);
        model.addAttribute("currentPuntuacionMin", currentPuntuacionMin);
        model.addAttribute("currentNTemporadasMin", currentNTemporadasMin);
        model.addAttribute("currentNTemporadasMax", currentNTemporadasMax);
        model.addAttribute("currentEstadoSerie", currentEstadoSerie);

        model.addAttribute("minAnioVal", 1950);
        model.addAttribute("maxAnioVal", Year.now().getValue());
        model.addAttribute("minNTemporadasVal", 1);
        model.addAttribute("maxNTemporadasVal", 50);

        String hxRequestHeader = request.getHeader("HX-Request");
        if (hxRequestHeader != null && hxRequestHeader.equals("true")) {
            response.setHeader("HX-Push-Url", "false");
            return "series/series :: #series-list-container";
        }
        return "series/series";
    }

    @GetMapping("/series/{id}")
    public String mostrarDetalleSerie(@PathVariable("id") Long id, Model model, HttpSession session) {
        Optional<Serie> serieOptional = serieService.findById(id);
        if (serieOptional.isPresent()) {
            Serie serie = serieOptional.get();
            model.addAttribute("serie", serie);
            model.addAttribute("currentPage", "series");
            model.addAttribute("pageTitle", serie.getTitulo() + " - WatchList");
            model.addAttribute("hideSidebar", true);

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

            List<Comentario> comentarios = comentarioService.obtenerComentariosPorMediaId(id);
            model.addAttribute("comentarios", comentarios);

            return "series/serie-detalle";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serie no encontrada");
        }
    }

    @PostMapping("/series/{serieId}/comentar")
    public String guardarComentarioSerie(@PathVariable Long serieId,
            @RequestParam String texto,
            @RequestParam int puntuacion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirectAttributes.addFlashAttribute("errorGeneral", "Debes iniciar sesión para comentar.");
            return "redirect:/login";
        }

        Optional<Serie> serieOpt = serieService.findById(serieId);
        if (serieOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorGeneral", "Serie no encontrada.");
            return "redirect:/series";
        }
        Serie serie = serieOpt.get();

        try {
            if (texto == null || texto.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorComentario",
                        "El texto del comentario no puede estar vacío.");
                redirectAttributes.addFlashAttribute("textoPrevio", texto);
                redirectAttributes.addFlashAttribute("puntuacionPrevia", puntuacion);
                return "redirect:/series/" + serieId;
            }
            if (puntuacion < 1 || puntuacion > 5) {
                redirectAttributes.addFlashAttribute("errorComentario", "La puntuación debe estar entre 1 y 5.");
                redirectAttributes.addFlashAttribute("textoPrevio", texto);
                redirectAttributes.addFlashAttribute("puntuacionPrevia", puntuacion);
                return "redirect:/series/" + serieId;
            }

            comentarioService.guardarComentario(serie, texto, puntuacion, usuarioId);
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
        return "redirect:/series/" + serieId;
    }

    @PostMapping("/series/{id}/comentarios/{comentarioId}/eliminar")
    public String eliminarComentarioSerie(
            @PathVariable("id") Long serieId,
            @PathVariable("comentarioId") Long comentarioId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Debes estar logueado para eliminar comentarios.");
            return "redirect:/series/" + serieId;
        }

        try {
            comentarioService.eliminarComentario(comentarioId, usuario);
            redirectAttributes.addFlashAttribute("successMessage", "Comentario eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el comentario: " + e.getMessage());
        }

        return "redirect:/series/" + serieId;
    }
}
