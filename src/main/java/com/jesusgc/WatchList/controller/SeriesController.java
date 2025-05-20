package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Serie;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.model.Comentario;
import com.jesusgc.WatchList.service.SerieService;
import com.jesusgc.WatchList.service.UsuarioService;
import com.jesusgc.WatchList.service.ListaService;
import com.jesusgc.WatchList.service.ComentarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Sort;
import jakarta.servlet.http.HttpServletRequest; 

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class SeriesController {

    private final SerieService serieService;
    private final UsuarioService usuarioService;
    private final ListaService listaService;
    private final ComentarioService comentarioService;

    public SeriesController(SerieService serieService, UsuarioService usuarioService,
            ListaService listaService, ComentarioService comentarioService) {
        this.serieService = serieService;
        this.usuarioService = usuarioService;
        this.listaService = listaService;
        this.comentarioService = comentarioService;
    }

    @GetMapping("/series")
    public String mostrarSeries(Model model,
                                @RequestParam(defaultValue = "puntuacion") String sortBy, 
                                @RequestParam(defaultValue = "desc") String sortDir,
                                HttpServletRequest request) {
        model.addAttribute("currentPage", "series");

        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = sortBy;
        if (!List.of("titulo", "anioEstreno", "puntuacion", "nTemporadas").contains(sortBy)) {
            sortProperty = "puntuacion"; 
        }
        Sort sort = Sort.by(direction, sortProperty);

        List<Serie> series = serieService.findAll(sort);
        model.addAttribute("series", series);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);

        String hxRequestHeader = request.getHeader("HX-Request");
        if (hxRequestHeader != null && hxRequestHeader.equals("true")) {
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

    @PostMapping("/serie/{serieId}/comentar")
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
                return "redirect:/serie/" + serieId;
            }
            if (puntuacion < 1 || puntuacion > 5) {
                redirectAttributes.addFlashAttribute("errorComentario", "La puntuación debe estar entre 1 y 5.");
                redirectAttributes.addFlashAttribute("textoPrevio", texto);
                redirectAttributes.addFlashAttribute("puntuacionPrevia", puntuacion);
                return "redirect:/serie/" + serieId;
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

        return "redirect:/serie/" + serieId;
    }
}
