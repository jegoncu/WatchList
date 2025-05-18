package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Pelicula;
import com.jesusgc.WatchList.service.PeliculaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Controller
public class PeliculasController {

    private final PeliculaService peliculaService;

    public PeliculasController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    @GetMapping("/peliculas")
    public String mostrarPeliculas(Model model) {
        model.addAttribute("currentPage", "peliculas");
        List<Pelicula> peliculas = peliculaService.findAll();
        model.addAttribute("peliculas", peliculas);
        return "peliculas/peliculas"; 
    }

    @GetMapping("/peliculas/{id}") 
    public String mostrarDetallePelicula(@PathVariable("id") Long id, Model model) {
        Optional<Pelicula> peliculaOptional = peliculaService.findById(id);
        if (peliculaOptional.isPresent()) {
            Pelicula pelicula = peliculaOptional.get();
            model.addAttribute("pelicula", pelicula);
            model.addAttribute("currentPage", "peliculas"); 
            model.addAttribute("pageTitle", pelicula.getTitulo() + " - WatchList");
            model.addAttribute("hideSidebar", true); 
            return "peliculas/pelicula-detalle"; 
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Película no encontrada");
        }
    }
}