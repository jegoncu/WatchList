package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Pelicula;
import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.service.PeliculaService;
import com.jesusgc.WatchList.service.GeneroService;
import com.jesusgc.WatchList.service.PlataformaService;
import com.jesusgc.WatchList.service.PersonaService;
import jakarta.validation.Valid;
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
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PeliculaService peliculaService;
    private final GeneroService generoService;
    private final PlataformaService plataformaService;
    private final PersonaService personaService;

    public AdminController(
            PeliculaService peliculaService,
            GeneroService generoService,
            PlataformaService plataformaService,
            PersonaService personaService) {
        this.peliculaService = peliculaService;
        this.generoService = generoService;
        this.plataformaService = plataformaService;
        this.personaService = personaService;
    }

    @GetMapping
    public String mostrarAdminPanel(Model model) {

        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Panel de Administración");
        return "admin";
    }

    private static final List<String> POSSIBLE_ROLES = Arrays.asList(
            "Dirección", "Producción", "Guión", "Reparto", "Fotografía", "Edición", "Composión", "Montaje",
            "Showrunner");

    @GetMapping("/peliculas/nuevo")
    public String mostrarFormularioNuevaPelicula(Model model) {
        model.addAttribute("pelicula", new Pelicula());
        model.addAttribute("allGeneros", generoService.findAll());
        model.addAttribute("allPlataformas", plataformaService.findAll());
        model.addAttribute("allPersonas", personaService.findAll());
        model.addAttribute("possibleRoles", POSSIBLE_ROLES); // ADD THIS LINE
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Añadir Nueva Película");
        model.addAttribute("formAction", "/admin/peliculas/nuevo");
        return "admin/peliculas/form-pelicula";
    }

    @PostMapping("/peliculas/nuevo")
    public String procesarNuevaPelicula(@Valid @ModelAttribute("pelicula") Pelicula pelicula,
            BindingResult result,
            @RequestParam(name = "generoIds", required = false) List<Long> generoIds,
            @RequestParam(name = "plataformaIds", required = false) List<Long> plataformaIds,
            @RequestParam(name = "persona_ids", required = false) List<Long> persona_ids,
            @RequestParam(name = "persona_roles", required = false) List<String> persona_roles,
            Model model,
            RedirectAttributes redirectAttributes) {

        Map<Long, String> personaRolesMap = new HashMap<>();
        if (persona_ids != null && persona_roles != null && persona_ids.size() == persona_roles.size()) {
            for (int i = 0; i < persona_ids.size(); i++) {
                if (persona_ids.get(i) != null && persona_roles.get(i) != null
                        && !persona_roles.get(i).trim().isEmpty()) {
                    personaRolesMap.put(persona_ids.get(i), persona_roles.get(i));
                }
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("allGeneros", generoService.findAll());
            model.addAttribute("allPlataformas", plataformaService.findAll());
            model.addAttribute("allPersonas", personaService.findAll());
            model.addAttribute("possibleRoles", POSSIBLE_ROLES); // ADD THIS LINE
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Añadir Nueva Película");
            model.addAttribute("formAction", "/admin/peliculas/nuevo");
            return "admin/peliculas/form-pelicula";
        }

        peliculaService.savePeliculaWithRelations(pelicula, generoIds, plataformaIds, personaRolesMap);
        redirectAttributes.addFlashAttribute("successMessage", "Película añadida correctamente.");
        return "redirect:/admin/peliculas";
    }

    @GetMapping("/peliculas")
    public String listarPeliculasAdmin(Model model) {
        model.addAttribute("peliculas", peliculaService.findAll());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Gestionar Películas");
        return "admin/peliculas/lista-peliculas";
    }

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
        model.addAttribute("possibleRoles", POSSIBLE_ROLES);
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Editar Película");
        model.addAttribute("formAction", "/admin/peliculas/editar/" + id);
        return "admin/peliculas/form-pelicula";
    }

    @PostMapping("/peliculas/editar/{id}")
    public String procesarEditarPelicula(@PathVariable("id") Long id,
            @Valid @ModelAttribute("pelicula") Pelicula peliculaForm,
            BindingResult result,
            @RequestParam(name = "generoIds", required = false) List<Long> generoIds,
            @RequestParam(name = "plataformaIds", required = false) List<Long> plataformaIds,
            @RequestParam(name = "persona_ids", required = false) List<Long> persona_ids,
            @RequestParam(name = "persona_roles", required = false) List<String> persona_roles,
            Model model,
            RedirectAttributes redirectAttributes) {

        Map<Long, String> personaRolesMap = new HashMap<>();
        if (persona_ids != null && persona_roles != null && persona_ids.size() == persona_roles.size()) {
            for (int i = 0; i < persona_ids.size(); i++) {
                if (persona_ids.get(i) != null && persona_roles.get(i) != null
                        && !persona_roles.get(i).trim().isEmpty()) {
                    personaRolesMap.put(persona_ids.get(i), persona_roles.get(i));
                }
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("allGeneros", generoService.findAll());
            model.addAttribute("allPlataformas", plataformaService.findAll());
            model.addAttribute("allPersonas", personaService.findAll());
            model.addAttribute("possibleRoles", POSSIBLE_ROLES);
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Editar Película");
            model.addAttribute("formAction", "/admin/peliculas/editar/" + id);
            return "admin/peliculas/form-pelicula";
        }

        peliculaService.updatePeliculaWithRelations(id, peliculaForm, generoIds, plataformaIds, personaRolesMap);
        redirectAttributes.addFlashAttribute("successMessage", "Película actualizada correctamente.");
        return "redirect:/admin/peliculas";
    }

    @PostMapping("/peliculas/eliminar/{id}")
    public String eliminarPelicula(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            peliculaService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Película eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la película: " + e.getMessage());
        }
        return "redirect:/admin/peliculas";
    }

    // --- Métodos para Gestionar Gente ---

    @GetMapping("/gente")
    public String listarGenteAdmin(Model model) {
        model.addAttribute("personas", personaService.findAll());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Gestionar Gente");
        return "admin/gente/lista-gente";
    }

    @GetMapping("/gente/nuevo")
    public String mostrarFormularioNuevaPersona(Model model) {
        model.addAttribute("persona", new Persona());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Añadir Nueva Persona");
        model.addAttribute("formAction", "/admin/gente/nuevo");
        return "admin/gente/form-persona";
    }

    @PostMapping("/gente/nuevo")
    public String procesarNuevaPersona(@Valid @ModelAttribute("persona") Persona persona,
                                       BindingResult result,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Añadir Nueva Persona");
            model.addAttribute("formAction", "/admin/gente/nuevo");
            return "admin/gente/form-persona";
        }
        personaService.save(persona);
        redirectAttributes.addFlashAttribute("successMessage", "Persona añadida correctamente.");
        return "redirect:/admin/gente";
    }

    @GetMapping("/gente/editar/{id}")
    public String mostrarFormularioEditarPersona(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Persona persona = personaService.findById(id).orElse(null);
        if (persona == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Persona no encontrada.");
            return "redirect:/admin/gente";
        }
        model.addAttribute("persona", persona);
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Editar Persona");
        model.addAttribute("formAction", "/admin/gente/editar/" + id);
        return "admin/gente/form-persona";
    }

    @PostMapping("/gente/editar/{id}")
    public String procesarEditarPersona(@PathVariable("id") Long id,
                                        @Valid @ModelAttribute("persona") Persona personaForm,
                                        BindingResult result,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (!personaService.findById(id).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Persona no encontrada.");
            return "redirect:/admin/gente";
        }

        if (result.hasErrors()) {
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Editar Persona");
            model.addAttribute("formAction", "/admin/gente/editar/" + id);
            // Ensure the ID is preserved in the form object if validation fails
            // personaForm.setId(id); // Not strictly necessary if th:object binds ID, but good for clarity
            return "admin/gente/form-persona";
        }
        personaForm.setId(id); // Ensure the ID is set for the update
        personaService.save(personaForm);
        redirectAttributes.addFlashAttribute("successMessage", "Persona actualizada correctamente.");
        return "redirect:/admin/gente";
    }

    @PostMapping("/gente/eliminar/{id}")
    public String eliminarPersona(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            personaService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Persona eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la persona: " + e.getMessage());
        }
        return "redirect:/admin/gente";
    }

    @GetMapping("/listas")
    public String listarListasAdmin(Model model) {
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Gestionar Listas");
        model.addAttribute("message", "Página de gestión de Listas en construcción.");
        return "admin/placeholder-list";
    }

    @GetMapping("/usuarios")
    public String listarUsuariosAdmin(Model model) {
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Gestionar Usuarios");
        model.addAttribute("message", "Página de gestión de Usuarios en construcción.");
        return "admin/placeholder-list";
    }

}
