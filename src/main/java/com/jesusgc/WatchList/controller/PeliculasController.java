package com.jesusgc.WatchList.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class PeliculasController {

    @GetMapping("/peliculas")
    public String mostrarPeliculas(Model model) {
        model.addAttribute("currentPage", "peliculas");
        return "peliculas";
    }
}