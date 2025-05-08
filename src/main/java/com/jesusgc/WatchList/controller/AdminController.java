package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Pelicula;
import com.jesusgc.WatchList.service.PeliculaService;
import com.jesusgc.WatchList.service.GeneroService;
import com.jesusgc.WatchList.service.PlataformaService;
import com.jesusgc.WatchList.service.UsuarioService;
import com.jesusgc.WatchList.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final PeliculaService peliculaService;
    private final GeneroService generoService;
    private final PlataformaService plataformaService;
    private final PersonaService personaService;

    @Autowired
    public AdminController(UsuarioService usuarioService,
            PeliculaService peliculaService,
            GeneroService generoService,
            PlataformaService plataformaService,
            PersonaService personaService) {
        this.usuarioService = usuarioService;
        this.peliculaService = peliculaService;
        this.generoService = generoService;
        this.plataformaService = plataformaService;
        this.personaService = personaService;
    }

    // Manejador de la página principal del panel de administración 
    @GetMapping
    public String mostrarAdminPanel(Model model) {
        
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Panel de Administración");
        return "admin"; 
    }

    // --- Películas CRUD ---

    @GetMapping("/peliculas/nuevo")
    public String mostrarFormularioNuevaPelicula(Model model) {
        model.addAttribute("pelicula", new Pelicula());
        model.addAttribute("allGeneros", generoService.findAll());
        model.addAttribute("allPlataformas", plataformaService.findAll());
        model.addAttribute("allPersonas", personaService.findAll());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Añadir Nueva Película");
        model.addAttribute("formAction", "/admin/peliculas/nuevo");
        return "admin/peliculas/form-pelicula";
    }

    // Añadir pelicula
    @PostMapping("/peliculas/nuevo")
    public String procesarNuevaPelicula(@Valid @ModelAttribute("pelicula") Pelicula pelicula,
            BindingResult result,
            @RequestParam(name = "generoIds", required = false) List<Long> generoIds,
            @RequestParam(name = "plataformaIds", required = false) List<Long> plataformaIds,
            @RequestParam(name = "personaIds", required = false) List<Long> personaIds,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allGeneros", generoService.findAll());
            model.addAttribute("allPlataformas", plataformaService.findAll());
            model.addAttribute("allPersonas", personaService.findAll());
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Añadir Nueva Película");
            model.addAttribute("formAction", "/admin/peliculas/nuevo");
            return "admin/peliculas/form-pelicula";
        }

        peliculaService.savePeliculaWithRelations(pelicula, generoIds, plataformaIds, personaIds);
        redirectAttributes.addFlashAttribute("successMessage", "Película añadida correctamente.");
        return "redirect:/admin/peliculas";
    }

    // Mostrar lista de peliculas
    @GetMapping("/peliculas")
    public String listarPeliculasAdmin(Model model) {
        model.addAttribute("peliculas", peliculaService.findAll());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Gestionar Películas");
        return "admin/peliculas/lista-peliculas";
    }

    // Mostrar formulario para editar pelicula
    @GetMapping("/peliculas/editar/{id}")
    public String mostrarFormularioEditarPelicula(@PathVariable("id") Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Pelicula pelicula = peliculaService.findById(id)
                .orElse(null);

        if (pelicula == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Película no encontrada.");
            return "redirect:/admin/peliculas";
        }

        model.addAttribute("pelicula", pelicula);
        model.addAttribute("allGeneros", generoService.findAll());
        model.addAttribute("allPlataformas", plataformaService.findAll());
        model.addAttribute("allPersonas", personaService.findAll());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Editar Película");
        model.addAttribute("formAction", "/admin/peliculas/editar/" + id);
        return "admin/peliculas/form-pelicula";
    }

    // Proceso de edición de película
    @PostMapping("/peliculas/editar/{id}")
    public String procesarEditarPelicula(@PathVariable("id") Long id,
            @Valid @ModelAttribute("pelicula") Pelicula peliculaForm,
            BindingResult result,
            @RequestParam(name = "generoIds", required = false) List<Long> generoIds,
            @RequestParam(name = "plataformaIds", required = false) List<Long> plataformaIds,
            @RequestParam(name = "personaIds", required = false) List<Long> personaIds,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("allGeneros", generoService.findAll());
            model.addAttribute("allPlataformas", plataformaService.findAll());
            model.addAttribute("allPersonas", personaService.findAll());
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Editar Película");
            model.addAttribute("formAction", "/admin/peliculas/editar/" + id);
            return "admin/peliculas/form-pelicula";
        }

        peliculaService.updatePeliculaWithRelations(id, peliculaForm, generoIds, plataformaIds, personaIds);
        redirectAttributes.addFlashAttribute("successMessage", "Película actualizada correctamente.");
        return "redirect:/admin/peliculas";
    }

    // Proceso de eliminación de película
    @PostMapping("/peliculas/eliminar/{id}")
    public String eliminarPelicula(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            peliculaService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Película eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la película.");
        }
        return "redirect:/admin/peliculas";
    }
}
