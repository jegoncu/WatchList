package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.service.PersonaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Controller
public class GenteController {

    private final PersonaService personaService;

    public GenteController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/gente")
    public String mostrarListaGente(Model model) {
        List<Persona> personas = personaService.findAll();
        model.addAttribute("personas", personas);
        model.addAttribute("currentPage", "gente");
        return "gente/gente";
    }

    @GetMapping("/gente/{id}") 
    public String mostrarDetallePersona(@PathVariable Long id, Model model) {
        Optional<Persona> personaOpt = personaService.findById(id);
        if (personaOpt.isPresent()) {
            Persona persona = personaOpt.get();
            model.addAttribute("persona", persona);
            model.addAttribute("currentPage", "gente");
            model.addAttribute("pageTitle", persona.getNombre() + " - WatchList");
            return "gente/persona-detalle";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada");
        }
    }
}
