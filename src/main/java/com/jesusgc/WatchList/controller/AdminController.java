package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Pelicula;
import com.jesusgc.WatchList.model.Persona;
import com.jesusgc.WatchList.model.Serie;
import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.service.PeliculaService;
import com.jesusgc.WatchList.service.GeneroService;
import com.jesusgc.WatchList.service.PlataformaService;
import com.jesusgc.WatchList.service.PersonaService;
import com.jesusgc.WatchList.service.SerieService;
import com.jesusgc.WatchList.service.ListaService;
import com.jesusgc.WatchList.service.UsuarioService;
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
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PeliculaService peliculaService;
    private final GeneroService generoService;
    private final PlataformaService plataformaService;
    private final PersonaService personaService;
    private final SerieService serieService;
    private final ListaService listaService;
    private final UsuarioService usuarioService;

    public AdminController(
            PeliculaService peliculaService,
            GeneroService generoService,
            PlataformaService plataformaService,
            PersonaService personaService,
            SerieService serieService,
            ListaService listaService,
            UsuarioService usuarioService) {
        this.peliculaService = peliculaService;
        this.generoService = generoService;
        this.plataformaService = plataformaService;
        this.personaService = personaService;
        this.serieService = serieService;
        this.listaService = listaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrarAdminPanel(Model model) {

        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Panel de Administración");
        return "admin/admin";
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
        model.addAttribute("possibleRoles", POSSIBLE_ROLES);
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
    public String mostrarFormularioEditarPersona(@PathVariable("id") Long id, Model model,
            RedirectAttributes redirectAttributes) {
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
            return "admin/gente/form-persona";
        }
        personaForm.setId(id);
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
        List<Lista> todasLasListas = listaService.findAll();
        model.addAttribute("listas", todasLasListas);
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Gestionar Listas");
        return "admin/listas/lista-listas";
    }

    @GetMapping("/listas/nueva")
    public String mostrarFormularioNuevaLista(Model model) {
        model.addAttribute("lista", new Lista());
        model.addAttribute("allUsuarios", usuarioService.findAll());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Crear Nueva Lista");
        model.addAttribute("formAction", "/admin/listas/nueva");
        return "admin/listas/form-lista";
    }

    @PostMapping("/listas/nueva")
    public String procesarNuevaLista(@Valid @ModelAttribute("lista") Lista lista,
            BindingResult result,
            @RequestParam("usuarioId") Long usuarioId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("allUsuarios", usuarioService.findAll());
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Crear Nueva Lista");
            model.addAttribute("formAction", "/admin/listas/nueva");
            return "admin/listas/form-lista";
        }

        try {
            Usuario usuario = usuarioService.findById(usuarioId);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }
            listaService.crearLista(lista.getTitulo(), lista.getEsPublica(), usuario);
            redirectAttributes.addFlashAttribute("successMessage", "Lista creada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la lista: " + e.getMessage());
        }

        return "redirect:/admin/listas";
    }

    @GetMapping("/listas/editar/{id}")
    public String mostrarFormularioEditarLista(@PathVariable("id") Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Lista> listaOpt = listaService.findById(id);
        if (listaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lista no encontrada.");
            return "redirect:/admin/listas";
        }

        model.addAttribute("lista", listaOpt.get());
        model.addAttribute("allUsuarios", usuarioService.findAll());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Editar Lista");
        model.addAttribute("formAction", "/admin/listas/editar/" + id);
        return "admin/listas/form-lista";
    }

    @PostMapping("/listas/editar/{id}")
    public String procesarEditarLista(@PathVariable("id") Long id,
            @Valid @ModelAttribute("lista") Lista listaForm,
            BindingResult result,
            @RequestParam("usuarioId") Long usuarioId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("allUsuarios", usuarioService.findAll());
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Editar Lista");
            model.addAttribute("formAction", "/admin/listas/editar/" + id);
            return "admin/listas/form-lista";
        }

        try {
            Usuario usuario = usuarioService.findById(usuarioId);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no encontrado");
            }
            listaService.updateLista(id, listaForm.getTitulo(), listaForm.getEsPublica(), usuario);
            redirectAttributes.addFlashAttribute("successMessage", "Lista actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la lista: " + e.getMessage());
        }

        return "redirect:/admin/listas";
    }

    @PostMapping("/listas/eliminar/{id}")
    public String eliminarLista(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            listaService.deleteById(id); // Necesitaremos añadir este método al servicio
            redirectAttributes.addFlashAttribute("successMessage", "Lista eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la lista: " + e.getMessage());
        }
        return "redirect:/admin/listas";
    }

    @GetMapping("/usuarios")
    public String listarUsuariosAdmin(Model model) {
        List<Usuario> todosLosUsuarios = usuarioService.findAll();
        model.addAttribute("usuarios", todosLosUsuarios);
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Gestionar Usuarios");
        return "admin/usuarios/lista-usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Crear Nuevo Usuario");
        model.addAttribute("formAction", "/admin/usuarios/nuevo");
        return "admin/usuarios/form-usuario";
    }

    @PostMapping("/usuarios/nuevo")
    public String procesarNuevoUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Crear Nuevo Usuario");
            model.addAttribute("formAction", "/admin/usuarios/nuevo");
            return "admin/usuarios/form-usuario";
        }

        try {
            usuarioService.registrar(usuario.getEmail(), usuario.getNombre(),
                               usuario.getContrasenia(), usuario.getEsPublico(), usuario.getEsAdmin());
            redirectAttributes.addFlashAttribute("successMessage", "Usuario creado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el usuario: " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable("id") Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.findByIdOptional(id); // Necesitaremos añadir este método
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/admin/usuarios";
        }

        model.addAttribute("usuario", usuarioOpt.get());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Editar Usuario");
        model.addAttribute("formAction", "/admin/usuarios/editar/" + id);
        return "admin/usuarios/form-usuario";
    }

    @PostMapping("/usuarios/editar/{id}")
    public String procesarEditarUsuario(@PathVariable("id") Long id,
            @Valid @ModelAttribute("usuario") Usuario usuarioForm,
            BindingResult result,
            @RequestParam(value = "nuevaContrasenia", required = false) String nuevaContrasenia,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("currentPage", "admin");
            model.addAttribute("pageTitle", "Editar Usuario");
            model.addAttribute("formAction", "/admin/usuarios/editar/" + id);
            return "admin/usuarios/form-usuario";
        }

        try {
            usuarioService.updateUsuario(id, usuarioForm.getNombre(), usuarioForm.getEmail(),
                                   nuevaContrasenia, usuarioForm.getEsPublico(), usuarioForm.getEsAdmin());
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el usuario: " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id); // Necesitaremos añadir este método
            redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/series")
    public String listarSeriesAdmin(Model model) {
        model.addAttribute("series", serieService.findAll());
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Gestionar Series");
        return "admin/series/lista-series";
    }

    @GetMapping("/series/nuevo")
    public String mostrarFormularioNuevaSerie(Model model) {
        model.addAttribute("serie", new Serie());
        model.addAttribute("allGeneros", generoService.findAll());
        model.addAttribute("allPlataformas", plataformaService.findAll());
        model.addAttribute("allPersonas", personaService.findAll());
        model.addAttribute("possibleRoles", POSSIBLE_ROLES);
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Añadir Nueva Serie");
        model.addAttribute("formAction", "/admin/series/nuevo");
        return "admin/series/form-serie";
    }

    @PostMapping("/series/nuevo")
    public String procesarNuevaSerie(@Valid @ModelAttribute("serie") Serie serie,
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
            model.addAttribute("pageTitle", "Añadir Nueva Serie");
            model.addAttribute("formAction", "/admin/series/nuevo");
            return "admin/series/form-serie";
        }

        serieService.saveSerieWithRelations(serie, generoIds, plataformaIds, personaRolesMap);
        redirectAttributes.addFlashAttribute("successMessage", "Serie añadida correctamente.");
        return "redirect:/admin/series";
    }

    @GetMapping("/series/editar/{id}")
    public String mostrarFormularioEditarSerie(@PathVariable("id") Long id, Model model,
            RedirectAttributes redirectAttributes) {
        Serie serie = serieService.findById(id)
                .orElse(null);

        if (serie == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Serie no encontrada.");
            return "redirect:/admin/series";
        }

        model.addAttribute("serie", serie);
        model.addAttribute("allGeneros", generoService.findAll());
        model.addAttribute("allPlataformas", plataformaService.findAll());
        model.addAttribute("allPersonas", personaService.findAll());
        model.addAttribute("possibleRoles", POSSIBLE_ROLES);
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Editar Serie");
        model.addAttribute("formAction", "/admin/series/editar/" + id);
        return "admin/series/form-serie";
    }

    @PostMapping("/series/editar/{id}")
    public String procesarEditarSerie(@PathVariable("id") Long id,
            @Valid @ModelAttribute("serie") Serie serieForm,
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
            model.addAttribute("pageTitle", "Editar Serie");
            model.addAttribute("formAction", "/admin/series/editar/" + id);
            return "admin/series/form-serie";
        }

        serieService.updateSerieWithRelations(id, serieForm, generoIds, plataformaIds, personaRolesMap);
        redirectAttributes.addFlashAttribute("successMessage", "Serie actualizada correctamente.");
        return "redirect:/admin/series";
    }

    @PostMapping("/series/eliminar/{id}")
    public String eliminarSerie(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            serieService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Serie eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la serie: " + e.getMessage());
        }
        return "redirect:/admin/series";
    }
}
