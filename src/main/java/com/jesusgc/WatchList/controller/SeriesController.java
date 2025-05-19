package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Serie;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.service.SerieService;
import com.jesusgc.WatchList.service.UsuarioService;
import com.jesusgc.WatchList.service.ListaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class SeriesController {

    private final SerieService serieService;
    private final UsuarioService usuarioService;
    private final ListaService listaService;

    public SeriesController(SerieService serieService, UsuarioService usuarioService, ListaService listaService) {
        this.serieService = serieService;
        this.usuarioService = usuarioService;
        this.listaService = listaService;
    }

    @GetMapping("/series")
    public String mostrarSeries(Model model) {
        List<Serie> series = serieService.findAll();
        model.addAttribute("series", series);
        model.addAttribute("currentPage", "series");
        return "series/series";
    }

    @GetMapping("/serie/{id}")
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
                    List<Lista> misListas = listaService.obtenerListasPorUsuario(usuarioLogueado);
                    model.addAttribute("misListas", misListas);
                } catch (IllegalArgumentException e) {
                    model.addAttribute("misListas", Collections.emptyList());
                }
            } else {
                model.addAttribute("misListas", Collections.emptyList());
            }

            return "series/serie-detalle";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serie no encontrada");
        }
    }
}
