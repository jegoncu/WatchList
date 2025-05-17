package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Serie;
import com.jesusgc.WatchList.service.SerieService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException; 
import org.springframework.http.HttpStatus; 

import java.util.List;
import java.util.Optional; 

@Controller
public class SeriesController {

    private final SerieService serieService;

    public SeriesController(SerieService serieService) {
        this.serieService = serieService;
    }

    @GetMapping("/series")
    public String mostrarSeries(Model model) {
        List<Serie> series = serieService.findAll();
        model.addAttribute("series", series);
        model.addAttribute("currentPage", "series");
        return "series/series"; 
    }

    @GetMapping("/serie/{id}")
    public String mostrarDetalleSerie(@PathVariable("id") Long id, Model model) {
        Optional<Serie> serieOptional = serieService.findById(id);
        if (serieOptional.isPresent()) {
            Serie serie = serieOptional.get();
            model.addAttribute("serie", serie);
            model.addAttribute("currentPage", "series"); 
            model.addAttribute("pageTitle", serie.getTitulo() + " - WatchList"); 
            return "series/serie-detalle"; 
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serie no encontrada");
        }
    }
}
