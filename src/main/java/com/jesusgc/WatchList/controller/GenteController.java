package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.service.PersonaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class GenteController {
    
    private final PersonaService personaService;
    
    public GenteController(PersonaService personaService) {
        this.personaService = personaService;
    }
    
    @GetMapping("/gente")
    public String mostrarGente(Model model) {
        List<Persona> personas = personaService.findAll();
        model.addAttribute("personas", personas);
        model.addAttribute("currentPage", "gente");
        return "gente";
    }
    
    // Esta función se puede implementar más adelante para mostrar los detalles de una persona
    @GetMapping("/gente/{id}")
    public String mostrarDetallesPersona(@PathVariable("id") Long id, Model model) {
        Persona persona = personaService.findById(id).orElse(null);
        if (persona == null) {
            return "redirect:/gente";
        }
        
        model.addAttribute("persona", persona);
        model.addAttribute("currentPage", "gente");
        model.addAttribute("pageTitle", persona.getNombre());
        return "persona-detalle"; 
    }
}
