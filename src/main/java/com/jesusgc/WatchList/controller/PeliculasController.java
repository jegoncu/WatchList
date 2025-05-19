package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Pelicula;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.service.PeliculaService;
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
public class PeliculasController {

    private final PeliculaService peliculaService;
    private final UsuarioService usuarioService;
    private final ListaService listaService;

    public PeliculasController(PeliculaService peliculaService, UsuarioService usuarioService,
            ListaService listaService) {
        this.peliculaService = peliculaService;
        this.usuarioService = usuarioService;
        this.listaService = listaService;
    }

    @GetMapping("/peliculas")
    public String mostrarPeliculas(Model model) {
        model.addAttribute("currentPage", "peliculas");
        List<Pelicula> peliculas = peliculaService.findAll();
        model.addAttribute("peliculas", peliculas);
        return "peliculas/peliculas";
    }

    @GetMapping("/peliculas/{id}")
    public String mostrarDetallePelicula(@PathVariable("id") Long id, Model model, HttpSession session) {
        Optional<Pelicula> peliculaOptional = peliculaService.findById(id);
        if (peliculaOptional.isPresent()) {
            Pelicula pelicula = peliculaOptional.get();
            model.addAttribute("pelicula", pelicula);
            model.addAttribute("currentPage", "peliculas");
            model.addAttribute("pageTitle", pelicula.getTitulo() + " - WatchList");
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

            return "peliculas/pelicula-detalle";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Película no encontrada");
        }
    }
}