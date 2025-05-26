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

@Controller
public class BuscarController {

    private final PeliculaService peliculaService;
    private final SerieService serieService;
    private final PersonaService personaService;
    private final ListaService listaService;

    public BuscarController(PeliculaService peliculaService, SerieService serieService,
                           PersonaService personaService, ListaService listaService) {
        this.peliculaService = peliculaService;
        this.serieService = serieService;
        this.personaService = personaService;
        this.listaService = listaService;
    }

    @GetMapping("/buscar")
    public String buscarTodo(@RequestParam("q") String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/";
        }

        String queryLimpio = query.trim();

        List<?> peliculas = peliculaService.buscarPorTitulo(queryLimpio);
        List<?> series = serieService.buscarPorTitulo(queryLimpio);
        List<?> personas = personaService.buscarPorNombre(queryLimpio);
        List<?> listas = listaService.buscarPorTitulo(queryLimpio);

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
