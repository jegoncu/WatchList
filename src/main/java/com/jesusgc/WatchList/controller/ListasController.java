package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Lista;
import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.service.ListaService;
import com.jesusgc.WatchList.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de listas de contenido multimedia.
 * Maneja la creación, visualización, edición y eliminación de listas de
 * usuarios.
 *
 * @author Jesús González Cuenca
 */
@Controller
public class ListasController {

  private final ListaService listaService;
  private final UsuarioService usuarioService;

  /**
   * Constructor del controlador con inyección de dependencias.
   *
   * @param listaService   Servicio de listas
   * @param usuarioService Servicio de usuarios
   */
  public ListasController(ListaService listaService, UsuarioService usuarioService) {
    this.listaService = listaService;
    this.usuarioService = usuarioService;
  }

  /**
   * Muestra las listas públicas o las listas del usuario según el filtro
   * aplicado.
   *
   * @param filtro  Filtro para mostrar listas específicas ("mis-listas" o null
   *                para públicas)
   * @param model   Modelo para la vista
   * @param session Sesión HTTP
   * @return Vista de listado de listas
   */
  @GetMapping("/listas")
  public String mostrarListasPublicas(@RequestParam(value = "filtro", required = false) String filtro,
      Model model, HttpSession session) {
    List<Lista> listas;
    String tituloPagina;

    // Determinar qué listas mostrar según el filtro
    if ("mis-listas".equals(filtro)) {
      Long usuarioId = (Long) session.getAttribute("usuarioId");
      if (usuarioId != null) {
        try {
          Usuario usuarioLogueado = usuarioService.findById(usuarioId);
          listas = listaService.obtenerListasPorUsuario(usuarioLogueado);
          tituloPagina = "Mis Listas";
        } catch (IllegalArgumentException e) {
          listas = listaService.obtenerListasPublicas();
          tituloPagina = "Listas Públicas";
        }
      } else {
        listas = listaService.obtenerListasPublicas();
        tituloPagina = "Listas Públicas";
      }
    } else {
      listas = listaService.obtenerListasPublicas();
      tituloPagina = "Listas Públicas";
    }

    model.addAttribute("listas", listas);
    model.addAttribute("tituloPagina", tituloPagina);
    model.addAttribute("currentPage", "listas");
    model.addAttribute("hideSidebar", true);
    model.addAttribute("filtroActual", filtro);
    return "listas/listas";
  }

  /**
   * Muestra el formulario para crear una nueva lista.
   *
   * @param model   Modelo para la vista
   * @param session Sesión HTTP
   * @return Vista del formulario de creación o redirección a login
   */
  @GetMapping("/listas/nueva")
  public String mostrarFormularioNuevaLista(Model model, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute("usuarioId");
    if (usuarioId == null) {
      return "redirect:/login";
    }
    model.addAttribute("lista", new Lista());
    model.addAttribute("tituloPagina", "Crear Nueva Lista");
    model.addAttribute("currentPage", "misListas");
    model.addAttribute("hideSidebar", true);
    return "listas/form-lista";
  }

  /**
   * Procesa la creación de una nueva lista.
   *
   * @param lista              Datos de la lista a crear
   * @param result             Resultado de validación
   * @param session            Sesión HTTP
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a mis listas si es exitoso, formulario con errores si no
   */
  @PostMapping("/listas/crear")
  public String procesarCrearLista(@Valid @ModelAttribute("lista") Lista lista,
      BindingResult result,
      HttpSession session,
      Model model,
      RedirectAttributes redirectAttributes) {
    Long usuarioId = (Long) session.getAttribute("usuarioId");
    if (usuarioId == null) {
      return "redirect:/login";
    }

    if (result.hasFieldErrors("titulo")) {
      model.addAttribute("tituloPagina", "Crear Nueva Lista");
      model.addAttribute("currentPage", "misListas");
      return "listas/form-lista";
    }

    try {
      Usuario usuarioLogueado = usuarioService.findById(usuarioId);
      listaService.crearLista(lista.getTitulo(), lista.getEsPublica(), usuarioLogueado);
      redirectAttributes.addFlashAttribute("mensajeExito", "Lista creada exitosamente.");
      return "redirect:/listas?filtro=mis-listas";
    } catch (IllegalArgumentException e) {
      session.invalidate();
      redirectAttributes.addFlashAttribute("error", "Error al crear la lista. Sesión inválida.");
      return "redirect:/login";
    } catch (Exception e) {
      model.addAttribute("error", "Error inesperado al crear la lista: " + e.getMessage());
      model.addAttribute("tituloPagina", "Crear Nueva Lista");
      return "listas/form-lista";
    }
  }

  /**
   * Muestra el detalle de una lista específica con control de privacidad.
   *
   * @param listaId            ID de la lista a mostrar
   * @param model              Modelo para la vista
   * @param session            Sesión HTTP
   * @param redirectAttributes Atributos para redirección
   * @return Vista de detalle de la lista o redirección con error
   */
  @GetMapping("/listas/{id}")
  public String mostrarDetalleLista(@PathVariable("id") Long listaId, Model model, HttpSession session,
      RedirectAttributes redirectAttributes) {
    Optional<Lista> listaOptional = listaService.findById(listaId);

    if (listaOptional.isEmpty()) {
      redirectAttributes.addFlashAttribute("error", "Lista no encontrada.");
      return "redirect:/listas";
    }

    Lista lista = listaOptional.get();
    Long usuarioIdSesion = (Long) session.getAttribute("usuarioId");
    boolean esPropietario = false;

    // Control de acceso para listas privadas
    if (!lista.getEsPublica()) {
      if (usuarioIdSesion == null) {
        redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver esta lista privada.");
        return "redirect:/login?redirectUrl=/listas/" + listaId;
      }
      try {
        Usuario usuarioLogueado = usuarioService.findById(usuarioIdSesion);
        if (lista.getUsuario() != null && lista.getUsuario().getId().equals(usuarioLogueado.getId())) {
          esPropietario = true;
        } else {
          redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver esta lista privada.");
          return "redirect:/listas";
        }
      } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", "Error de autenticación. Por favor, inicia sesión de nuevo.");
        session.invalidate();
        return "redirect:/login";
      }
    } else {
      // Verificar propiedad para listas públicas
      if (usuarioIdSesion != null && lista.getUsuario() != null && lista.getUsuario().getId().equals(usuarioIdSesion)) {
        esPropietario = true;
      }
    }

    model.addAttribute("lista", lista);
    model.addAttribute("tituloPagina", "Detalle: " + lista.getTitulo());
    model.addAttribute("esPropietario", esPropietario);
    model.addAttribute("hideSidebar", true);

    if (esPropietario) {
      model.addAttribute("currentPage", "misListas");
    } else {
      model.addAttribute("currentPage", "listasPublicas");
    }

    // Transferir mensajes flash al modelo
    if (redirectAttributes.getFlashAttributes().containsKey("mensajeExito")) {
      model.addAttribute("mensajeExito", redirectAttributes.getFlashAttributes().get("mensajeExito"));
    }
    if (redirectAttributes.getFlashAttributes().containsKey("error")) {
      model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
    }

    return "listas/lista-detalle";
  }

  /**
   * Procesa la eliminación de una lista del usuario.
   *
   * @param listaId            ID de la lista a eliminar
   * @param session            Sesión HTTP
   * @param redirectAttributes Atributos para redirección
   * @return Redirección a mis listas con mensaje de estado
   */
  @PostMapping("/listas/{id}/eliminar")
  public String procesarEliminarLista(@PathVariable("id") Long listaId,
      HttpSession session,
      RedirectAttributes redirectAttributes) {
    Long usuarioId = (Long) session.getAttribute("usuarioId");
    if (usuarioId == null) {
      return "redirect:/login";
    }
    try {
      Usuario usuarioLogueado = usuarioService.findById(usuarioId);
      listaService.eliminarLista(listaId, usuarioLogueado);
      redirectAttributes.addFlashAttribute("mensajeExito", "Lista eliminada exitosamente.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/listas?filtro=mis-listas";
  }

  /**
   * Agrega un elemento multimedia a una lista del usuario.
   *
   * @param listaId            ID de la lista
   * @param mediaId            ID del elemento multimedia
   * @param session            Sesión HTTP
   * @param redirectAttributes Atributos para redirección
   * @return Redirección al detalle de la lista con mensaje de estado
   */
  @PostMapping("/listas/{listaId}/media/{mediaId}/agregar")
  public String agregarMediaALista(@PathVariable("listaId") Long listaId,
      @PathVariable("mediaId") Long mediaId,
      HttpSession session,
      RedirectAttributes redirectAttributes) {
    Long usuarioId = (Long) session.getAttribute("usuarioId");
    if (usuarioId == null) {
      redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para modificar listas.");
      return "redirect:/login?redirectUrl=/listas/" + listaId;
    }

    try {
      Usuario usuarioLogueado = usuarioService.findById(usuarioId);
      listaService.agregarMediaALista(listaId, mediaId, usuarioLogueado);
      redirectAttributes.addFlashAttribute("mensajeExito", "Ítem añadido a la lista exitosamente.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error inesperado al añadir el ítem a la lista.");
    }
    return "redirect:/listas/" + listaId;
  }

  /**
   * Quita un elemento multimedia de una lista del usuario.
   *
   * @param listaId            ID de la lista
   * @param mediaId            ID del elemento multimedia
   * @param session            Sesión HTTP
   * @param redirectAttributes Atributos para redirección
   * @return Redirección al detalle de la lista con mensaje de estado
   */
  @PostMapping("/listas/{listaId}/media/{mediaId}/quitar")
  public String quitarMediaDeLista(@PathVariable("listaId") Long listaId,
      @PathVariable("mediaId") Long mediaId,
      HttpSession session,
      RedirectAttributes redirectAttributes) {
    Long usuarioId = (Long) session.getAttribute("usuarioId");
    if (usuarioId == null) {
      redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para modificar listas.");
      return "redirect:/login?redirectUrl=/listas/" + listaId;
    }

    try {
      Usuario usuarioLogueado = usuarioService.findById(usuarioId);
      listaService.quitarMediaDeLista(listaId, mediaId, usuarioLogueado);
      redirectAttributes.addFlashAttribute("mensajeExito", "Ítem quitado de la lista exitosamente.");
    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Error inesperado al quitar el ítem de la lista.");
    }
    return "redirect:/listas/" + listaId;
  }

  /**
   * Muestra el formulario de edición de una lista.
   *
   * @param id      ID de la lista a editar
   * @param model   Modelo para la vista
   * @param session Sesión HTTP
   * @return Vista del formulario de edición o redirección con error
   */
  @GetMapping("/listas/{id}/editar")
  public String mostrarFormularioEditarLista(@PathVariable Long id, Model model, HttpSession session) {
    Long usuarioId = (Long) session.getAttribute("usuarioId");
    if (usuarioId == null) {
      return "redirect:/login";
    }

    try {
      Optional<Lista> listaOptional = listaService.findById(id);
      if (listaOptional.isEmpty()) {
        return "redirect:/listas?error=Lista no encontrada";
      }

      Lista lista = listaOptional.get();
      Usuario usuarioLogueado = usuarioService.findById(usuarioId);

      // Verificar que el usuario es el propietario
      if (!lista.getUsuario().getId().equals(usuarioLogueado.getId())) {
        return "redirect:/listas/" + id + "?error=No tienes permisos para editar esta lista";
      }

      model.addAttribute("lista", lista);
      model.addAttribute("currentPage", "listas");
      model.addAttribute("tituloPagina", "Editar Lista");
      model.addAttribute("hideSidebar", true);

      return "listas/form-lista";

    } catch (IllegalArgumentException e) {
      return "redirect:/listas?error=Error al cargar la lista";
    }
  }

  /**
   * Procesa la actualización de una lista existente.
   *
   * @param id                 ID de la lista a actualizar
   * @param lista              Datos actualizados de la lista
   * @param result             Resultado de validación
   * @param session            Sesión HTTP
   * @param model              Modelo para la vista
   * @param redirectAttributes Atributos para redirección
   * @return Redirección al detalle si es exitoso, formulario con errores si no
   */
  @PostMapping("/listas/{id}/editar")
  public String procesarEditarLista(@PathVariable Long id,
      @Valid @ModelAttribute("lista") Lista lista,
      BindingResult result,
      HttpSession session,
      Model model,
      RedirectAttributes redirectAttributes) {
    Long usuarioId = (Long) session.getAttribute("usuarioId");
    if (usuarioId == null) {
      return "redirect:/login";
    }

    if (result.hasFieldErrors("titulo")) {
      model.addAttribute("tituloPagina", "Editar Lista");
      model.addAttribute("currentPage", "listas");
      model.addAttribute("hideSidebar", true);
      return "listas/form-lista";
    }

    try {
      Optional<Lista> listaOptional = listaService.findById(id);
      if (listaOptional.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Lista no encontrada");
        return "redirect:/listas";
      }

      Lista listaExistente = listaOptional.get();
      Usuario usuarioLogueado = usuarioService.findById(usuarioId);

      // Verificar que el usuario es el propietario
      if (!listaExistente.getUsuario().getId().equals(usuarioLogueado.getId())) {
        redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar esta lista");
        return "redirect:/listas/" + id;
      }

      // Actualizar la lista
      listaService.updateLista(id, lista.getTitulo(), lista.getEsPublica(), usuarioLogueado);
      redirectAttributes.addFlashAttribute("mensajeExito", "Lista actualizada correctamente");

      return "redirect:/listas/" + id;

    } catch (IllegalArgumentException e) {
      redirectAttributes.addFlashAttribute("error", "Error al actualizar la lista: " + e.getMessage());
      return "redirect:/listas/" + id;
    }
  }
}
