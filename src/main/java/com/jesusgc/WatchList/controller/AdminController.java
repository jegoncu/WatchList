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

/**
 * Controlador para el panel de administración del sistema.
 * Gestiona operaciones CRUD para todas las entidades: películas, series,
 * personas, listas y usuarios.
 *
 * @author Jesús González Cuenca
 */
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

  /**
   * Roles posibles para asignar a personas en créditos de películas y series.
   */
  private static final List<String> POSSIBLE_ROLES = Arrays.asList(
      "Dirección", "Producción", "Guión", "Reparto", "Fotografía", "Edición", "Composión", "Montaje",
      "Showrunner");

  /**
   * Constructor del controlador con inyección de dependencias.
   *
   * @param peliculaService   Servicio de películas
   * @param generoService     Servicio de géneros
   * @param plataformaService Servicio de plataformas
   * @param personaService    Servicio de personas
   * @param serieService      Servicio de series
   * @param listaService      Servicio de listas
   * @param usuarioService    Servicio de usuarios
   */
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

  /**
   * Muestra el panel principal de administración.
   *
   * @param model Modelo para la vista
   * @return Vista del panel de administración
   */
  @GetMapping
  public String mostrarAdminPanel(Model model) {
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Panel de Administración");
    return "admin/admin";
  }

  // GESTIÓN DE PELÍCULAS

  /**
   * Muestra el formulario para crear una nueva película.
   *
   * @param model Modelo para la vista
   * @return Vista del formulario de nueva película
   */
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

  /**
   * Procesa la creación de una nueva película con sus relaciones.
   *
   * @param pelicula           Datos de la película
   * @param result             Resultado de validación
   * @param generoIds          Lista de IDs de géneros asociados
   * @param plataformaIds      Lista de IDs de plataformas asociadas
   * @param persona_ids        Lista de IDs de personas para créditos
   * @param persona_roles      Lista de roles correspondientes a las personas
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de películas si es exitoso, formulario con
   *         errores si no
   */
  @PostMapping("/peliculas/nuevo")
  public String procesarNuevaPelicula(@Valid @ModelAttribute("pelicula") Pelicula pelicula,
      BindingResult result,
      @RequestParam(name = "generoIds", required = false) List<Long> generoIds,
      @RequestParam(name = "plataformaIds", required = false) List<Long> plataformaIds,
      @RequestParam(name = "persona_ids", required = false) List<Long> persona_ids,
      @RequestParam(name = "persona_roles", required = false) List<String> persona_roles,
      Model model,
      RedirectAttributes redirectAttributes) {

    // Construir mapa de persona-rol para créditos
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
      model.addAttribute("pageTitle", "Añadir Nueva Película");
      model.addAttribute("formAction", "/admin/peliculas/nuevo");
      return "admin/peliculas/form-pelicula";
    }

    peliculaService.savePeliculaWithRelations(pelicula, generoIds, plataformaIds, personaRolesMap);
    redirectAttributes.addFlashAttribute("successMessage", "Película añadida correctamente.");
    return "redirect:/admin/peliculas";
  }

  /**
   * Lista todas las películas del sistema para administración.
   *
   * @param model Modelo para la vista
   * @return Vista de lista de películas administrativas
   */
  @GetMapping("/peliculas")
  public String listarPeliculasAdmin(Model model) {
    model.addAttribute("peliculas", peliculaService.findAll());
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Gestionar Películas");
    return "admin/peliculas/lista-peliculas";
  }

  /**
   * Muestra el formulario para editar una película existente.
   *
   * @param id                 ID de la película a editar
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Vista del formulario de edición o redirección si no existe
   */
  @GetMapping("/peliculas/editar/{id}")
  public String mostrarFormularioEditarPelicula(@PathVariable("id") Long id, Model model,
      RedirectAttributes redirectAttributes) {

    Optional<Pelicula> peliculaOpt = peliculaService.findById(id);

    if (peliculaOpt.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorMessage", "Película no encontrada.");
      return "redirect:/admin/peliculas";
    }

    Pelicula pelicula = peliculaOpt.get();

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

  /**
   * Procesa la actualización de una película existente.
   *
   * @param id                 ID de la película a actualizar
   * @param peliculaForm       Datos actualizados de la película
   * @param result             Resultado de validación
   * @param generoIds          Lista de IDs de géneros asociados
   * @param plataformaIds      Lista de IDs de plataformas asociadas
   * @param persona_ids        Lista de IDs de personas para créditos
   * @param persona_roles      Lista de roles correspondientes a las personas
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de películas si es exitoso, formulario con
   *         errores si no
   */
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

    // Construir mapa de persona-rol validando longitudes
    Map<Long, String> personaRolesMap = new HashMap<>();
    if (persona_ids != null && persona_roles != null) {
      int minSize = Math.min(persona_ids.size(), persona_roles.size());
      for (int i = 0; i < minSize; i++) {
        Long personaId = persona_ids.get(i);
        String rol = persona_roles.get(i);

        if (personaId != null && rol != null && !rol.trim().isEmpty()) {
          personaRolesMap.put(personaId, rol.trim());
        }
      }
    }

    try {
      peliculaService.updatePeliculaWithRelations(id, peliculaForm, generoIds, plataformaIds, personaRolesMap);
      redirectAttributes.addFlashAttribute("successMessage", "Película actualizada correctamente.");
      return "redirect:/admin/peliculas";

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la película: " + e.getMessage());
      return "redirect:/admin/peliculas/editar/" + id;
    }
  }

  /**
   * Elimina una película del sistema.
   *
   * @param id                 ID de la película a eliminar
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de películas con mensaje de estado
   */
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

  // GESTIÓN DE PERSONAS

  /**
   * Lista todas las personas del sistema para administración.
   *
   * @param model Modelo para la vista
   * @return Vista de lista de personas administrativas
   */
  @GetMapping("/gente")
  public String listarGenteAdmin(Model model) {
    model.addAttribute("personas", personaService.findAll());
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Gestionar Gente");
    return "admin/gente/lista-gente";
  }

  /**
   * Muestra el formulario para crear una nueva persona.
   *
   * @param model Modelo para la vista
   * @return Vista del formulario de nueva persona
   */
  @GetMapping("/gente/nuevo")
  public String mostrarFormularioNuevaPersona(Model model) {
    model.addAttribute("persona", new Persona());
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Añadir Nueva Persona");
    model.addAttribute("formAction", "/admin/gente/nuevo");
    return "admin/gente/form-persona";
  }

  /**
   * Procesa la creación de una nueva persona.
   *
   * @param persona            Datos de la persona
   * @param result             Resultado de validación
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de personas si es exitoso, formulario con
   *         errores si no
   */
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

  /**
   * Muestra el formulario para editar una persona existente.
   *
   * @param id                 ID de la persona a editar
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Vista del formulario de edición o redirección si no existe
   */
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

  /**
   * Procesa la actualización de una persona existente.
   *
   * @param id                 ID de la persona a actualizar
   * @param personaForm        Datos actualizados de la persona
   * @param result             Resultado de validación
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de personas si es exitoso, formulario con
   *         errores si no
   */
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

  /**
   * Elimina una persona del sistema.
   *
   * @param id                 ID de la persona a eliminar
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de personas con mensaje de estado
   */
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

  // GESTIÓN DE LISTAS

  /**
   * Lista todas las listas del sistema para administración.
   *
   * @param model Modelo para la vista
   * @return Vista de lista de listas administrativas
   */
  @GetMapping("/listas")
  public String listarListasAdmin(Model model) {
    List<Lista> todasLasListas = listaService.findAll();
    model.addAttribute("listas", todasLasListas);
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Gestionar Listas");
    return "admin/listas/lista-listas";
  }

  /**
   * Muestra el formulario para crear una nueva lista.
   *
   * @param model Modelo para la vista
   * @return Vista del formulario de nueva lista
   */
  @GetMapping("/listas/nueva")
  public String mostrarFormularioNuevaLista(Model model) {
    model.addAttribute("lista", new Lista());
    model.addAttribute("allUsuarios", usuarioService.findAll());
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Crear Nueva Lista");
    model.addAttribute("formAction", "/admin/listas/nueva");
    return "admin/listas/form-lista";
  }

  /**
   * Procesa la creación de una nueva lista asignándola a un usuario.
   *
   * @param lista              Datos de la lista
   * @param result             Resultado de validación
   * @param usuarioId          ID del usuario propietario de la lista
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de listas si es exitoso, formulario con
   *         errores si no
   */
  @PostMapping("/listas/nueva")
  public String procesarNuevaLista(@Valid @ModelAttribute("lista") Lista lista,
      BindingResult result,
      @RequestParam("usuarioId") Long usuarioId,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (result.hasFieldErrors("titulo")) {
      model.addAttribute("allUsuarios", usuarioService.findAll());
      model.addAttribute("currentPage", "admin");
      model.addAttribute("pageTitle", "Crear Nueva Lista");
      model.addAttribute("formAction", "/admin/listas/nueva");
      return "admin/listas/form-lista";
    }

    if (usuarioId == null) {
      model.addAttribute("errorMessage", "Debe seleccionar un propietario para la lista.");
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
      return "redirect:/admin/listas";

    } catch (Exception e) {
      model.addAttribute("errorMessage", "Error al crear la lista: " + e.getMessage());
      model.addAttribute("allUsuarios", usuarioService.findAll());
      model.addAttribute("currentPage", "admin");
      model.addAttribute("pageTitle", "Crear Nueva Lista");
      model.addAttribute("formAction", "/admin/listas/nueva");
      return "admin/listas/form-lista";
    }
  }

  /**
   * Muestra el formulario para editar una lista existente.
   *
   * @param id                 ID de la lista a editar
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Vista del formulario de edición o redirección si no existe
   */
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

  /**
   * Procesa la actualización de una lista existente.
   *
   * @param id                 ID de la lista a actualizar
   * @param listaForm          Datos actualizados de la lista
   * @param result             Resultado de validación
   * @param usuarioId          ID del usuario propietario de la lista
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de listas si es exitoso, formulario con
   *         errores si no
   */
  @PostMapping("/listas/editar/{id}")
  public String procesarEditarLista(@PathVariable("id") Long id,
      @Valid @ModelAttribute("lista") Lista listaForm,
      BindingResult result,
      @RequestParam("usuarioId") Long usuarioId,
      Model model,
      RedirectAttributes redirectAttributes) {

    if (result.hasFieldErrors("titulo")) {
      model.addAttribute("allUsuarios", usuarioService.findAll());
      model.addAttribute("currentPage", "admin");
      model.addAttribute("pageTitle", "Editar Lista");
      model.addAttribute("formAction", "/admin/listas/editar/" + id);
      return "admin/listas/form-lista";
    }

    if (usuarioId == null) {
      model.addAttribute("errorMessage", "Debe seleccionar un propietario para la lista.");
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
      return "redirect:/admin/listas";

    } catch (Exception e) {
      model.addAttribute("errorMessage", "Error al actualizar la lista: " + e.getMessage());
      model.addAttribute("allUsuarios", usuarioService.findAll());
      model.addAttribute("currentPage", "admin");
      model.addAttribute("pageTitle", "Editar Lista");
      model.addAttribute("formAction", "/admin/listas/editar/" + id);
      return "admin/listas/form-lista";
    }
  }

  /**
   * Elimina una lista del sistema.
   *
   * @param id                 ID de la lista a eliminar
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de listas con mensaje de estado
   */
  @PostMapping("/listas/eliminar/{id}")
  public String eliminarLista(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    try {
      listaService.deleteById(id);
      redirectAttributes.addFlashAttribute("successMessage", "Lista eliminada correctamente.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la lista: " + e.getMessage());
    }
    return "redirect:/admin/listas";
  }

  // GESTIÓN DE USUARIOS

  /**
   * Lista todos los usuarios del sistema para administración.
   *
   * @param model Modelo para la vista
   * @return Vista de lista de usuarios administrativos
   */
  @GetMapping("/usuarios")
  public String listarUsuariosAdmin(Model model) {
    List<Usuario> todosLosUsuarios = usuarioService.findAll();
    model.addAttribute("usuarios", todosLosUsuarios);
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Gestionar Usuarios");
    return "admin/usuarios/lista-usuarios";
  }

  /**
   * Muestra el formulario para crear un nuevo usuario.
   *
   * @param model Modelo para la vista
   * @return Vista del formulario de nuevo usuario
   */
  @GetMapping("/usuarios/nuevo")
  public String mostrarFormularioNuevoUsuario(Model model) {
    model.addAttribute("usuario", new Usuario());
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Crear Nuevo Usuario");
    model.addAttribute("formAction", "/admin/usuarios/nuevo");
    return "admin/usuarios/form-usuario";
  }

  /**
   * Procesa la creación de un nuevo usuario con permisos administrativos
   * opcionales.
   *
   * @param usuario            Datos del usuario
   * @param result             Resultado de validación
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de usuarios si es exitoso, formulario con
   *         errores si no
   */
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

  /**
   * Muestra el formulario para editar un usuario existente.
   *
   * @param id                 ID del usuario a editar
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Vista del formulario de edición o redirección si no existe
   */
  @GetMapping("/usuarios/editar/{id}")
  public String mostrarFormularioEditarUsuario(@PathVariable("id") Long id, Model model,
      RedirectAttributes redirectAttributes) {
    Optional<Usuario> usuarioOpt = usuarioService.findByIdOptional(id);
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

  /**
   * Procesa la actualización de un usuario existente.
   *
   * @param id                 ID del usuario a actualizar
   * @param usuarioForm        Datos actualizados del usuario
   * @param result             Resultado de validación
   * @param nuevaContrasenia   Nueva contraseña opcional
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de usuarios si es exitoso, formulario con
   *         errores si no
   */
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

  /**
   * Elimina un usuario del sistema.
   *
   * @param id                 ID del usuario a eliminar
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de usuarios con mensaje de estado
   */
  @PostMapping("/usuarios/eliminar/{id}")
  public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
    try {
      usuarioService.deleteById(id);
      redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el usuario: " + e.getMessage());
    }
    return "redirect:/admin/usuarios";
  }

  // GESTIÓN DE SERIES

  /**
   * Lista todas las series del sistema para administración.
   *
   * @param model Modelo para la vista
   * @return Vista de lista de series administrativas
   */
  @GetMapping("/series")
  public String listarSeriesAdmin(Model model) {
    model.addAttribute("series", serieService.findAll());
    model.addAttribute("currentPage", "admin");
    model.addAttribute("pageTitle", "Gestionar Series");
    return "admin/series/lista-series";
  }

  /**
   * Muestra el formulario para crear una nueva serie.
   *
   * @param model Modelo para la vista
   * @return Vista del formulario de nueva serie
   */
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

  /**
   * Procesa la creación de una nueva serie con sus relaciones.
   *
   * @param serie              Datos de la serie
   * @param result             Resultado de validación
   * @param generoIds          Lista de IDs de géneros asociados
   * @param plataformaIds      Lista de IDs de plataformas asociadas
   * @param persona_ids        Lista de IDs de personas para créditos
   * @param persona_roles      Lista de roles correspondientes a las personas
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de series si es exitoso, formulario con
   *         errores si no
   */
  @PostMapping("/series/nuevo")
  public String procesarNuevaSerie(@Valid @ModelAttribute("serie") Serie serie,
      BindingResult result,
      @RequestParam(name = "generoIds", required = false) List<Long> generoIds,
      @RequestParam(name = "plataformaIds", required = false) List<Long> plataformaIds,
      @RequestParam(name = "persona_ids", required = false) List<Long> persona_ids,
      @RequestParam(name = "persona_roles", required = false) List<String> persona_roles,
      Model model,
      RedirectAttributes redirectAttributes) {

    // Construir mapa de persona-rol para créditos
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

  /**
   * Muestra el formulario para editar una serie existente.
   *
   * @param id                 ID de la serie a editar
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Vista del formulario de edición o redirección si no existe
   */
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

  /**
   * Procesa la actualización de una serie existente.
   *
   * @param id                 ID de la serie a actualizar
   * @param serieForm          Datos actualizados de la serie
   * @param result             Resultado de validación
   * @param generoIds          Lista de IDs de géneros asociados
   * @param plataformaIds      Lista de IDs de plataformas asociadas
   * @param persona_ids        Lista de IDs de personas para créditos
   * @param persona_roles      Lista de roles correspondientes a las personas
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de series si es exitoso, formulario con
   *         errores si no
   */
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

    // Construir mapa de persona-rol para créditos
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

  /**
   * Elimina una serie del sistema.
   *
   * @param id                 ID de la serie a eliminar
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a la lista de series con mensaje de estado
   */
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
