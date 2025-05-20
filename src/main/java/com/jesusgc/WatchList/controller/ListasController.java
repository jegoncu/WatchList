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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional; // Asegúrate de tener esta importación

@Controller
public class ListasController {

    private final ListaService listaService;
    private final UsuarioService usuarioService;

    public ListasController(ListaService listaService, UsuarioService usuarioService) {
        this.listaService = listaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listas")
    public String mostrarListasPublicas(Model model) {
        List<Lista> listasPublicas = listaService.obtenerListasPublicas();
        model.addAttribute("listas", listasPublicas);
        model.addAttribute("tituloPagina", "Listas Públicas");
        model.addAttribute("currentPage", "listasPublicas");
        return "listas/listas";
    }

    @GetMapping("/mis-listas")
    public String mostrarMisListas(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        try {
            Usuario usuarioLogueado = usuarioService.findById(usuarioId);
            List<Lista> misListas = listaService.obtenerListasPorUsuario(usuarioLogueado);
            model.addAttribute("listas", misListas);
            model.addAttribute("usuario", usuarioLogueado);
            model.addAttribute("tituloPagina", "Mis Listas");
            model.addAttribute("currentPage", "misListas");
            return "usuario/mis-listas";
        } catch (IllegalArgumentException e) {
            session.invalidate();
            return "redirect:/login?error=usuarioNoEncontrado";
        }
    }

    @GetMapping("/listas/nueva")
    public String mostrarFormularioNuevaLista(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        model.addAttribute("lista", new Lista());
        model.addAttribute("tituloPagina", "Crear Nueva Lista");
        model.addAttribute("currentPage", "misListas");
        return "listas/form-lista";
    }

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
            return "redirect:/mis-listas";
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

    @GetMapping("/listas/{id}")
    public String mostrarDetalleLista(@PathVariable("id") Long listaId, Model model, HttpSession session, RedirectAttributes redirectAttributes) { // Añadido RedirectAttributes
        Optional<Lista> listaOptional = listaService.findById(listaId);

        if (listaOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Lista no encontrada.");
            return "redirect:/listas"; // Redirige a la lista de listas públicas o a una página de error general
        }

        Lista lista = listaOptional.get();
        Long usuarioIdSesion = (Long) session.getAttribute("usuarioId");
        boolean esPropietario = false;

        // Verificar si la lista es privada
        if (!lista.getEsPublica()) {
            if (usuarioIdSesion == null) {
                // Usuario no logueado intentando acceder a lista privada
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver esta lista privada.");
                return "redirect:/login?redirectUrl=/listas/" + listaId; // Guardar la URL para redirigir después del login
            }
            // Usuario logueado, verificar si es el propietario
            try {
                Usuario usuarioLogueado = usuarioService.findById(usuarioIdSesion);
                if (lista.getUsuario() != null && lista.getUsuario().getId().equals(usuarioLogueado.getId())) {
                    esPropietario = true;
                } else {
                    // Usuario logueado pero no es el propietario de la lista privada
                    redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver esta lista privada.");
                    return "redirect:/listas"; // O a /mis-listas si tiene más sentido
                }
            } catch (IllegalArgumentException e) {
                // Error al buscar el usuario (ej. sesión inválida pero con ID)
                redirectAttributes.addFlashAttribute("error", "Error de autenticación. Por favor, inicia sesión de nuevo.");
                session.invalidate();
                return "redirect:/login";
            }
        } else { // La lista es pública
            if (usuarioIdSesion != null && lista.getUsuario() != null && lista.getUsuario().getId().equals(usuarioIdSesion)) {
                esPropietario = true; // El usuario logueado es el propietario de esta lista pública
            }
            // Si es pública y el usuario no está logueado, o está logueado y no es el propietario,
            // simplemente se muestra. esPropietario ya está en false o se actualizó.
        }

        // Si llegamos aquí, el usuario tiene permiso para ver la lista
        model.addAttribute("lista", lista);
        model.addAttribute("tituloPagina", "Detalle: " + lista.getTitulo()); // Título más específico
        model.addAttribute("esPropietario", esPropietario);

        // Determinar currentPage
        if (esPropietario) {
            model.addAttribute("currentPage", "misListas");
        } else {
            // Para una lista pública vista por otro usuario o un anónimo
            model.addAttribute("currentPage", "listasPublicas");
        }
        
        // Añadir mensajes flash si vienen de otra redirección (ej. después de agregar/quitar item)
        if (redirectAttributes.getFlashAttributes().containsKey("mensajeExito")) {
            model.addAttribute("mensajeExito", redirectAttributes.getFlashAttributes().get("mensajeExito"));
        }
        if (redirectAttributes.getFlashAttributes().containsKey("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }


        return "listas/lista-detalle";
    }

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
        return "redirect:/mis-listas";
    }

    @PostMapping("/listas/{listaId}/media/{mediaId}/agregar")
    public String agregarMediaALista(@PathVariable("listaId") Long listaId,
            @PathVariable("mediaId") Long mediaId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            // Si un usuario no logueado intenta agregar a una lista (no debería ser posible desde la UI)
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para modificar listas.");
            return "redirect:/login?redirectUrl=/listas/" + listaId;
        }

        try {
            Usuario usuarioLogueado = usuarioService.findById(usuarioId);
            // El servicio agregarMediaALista ya verifica si el usuario es propietario
            listaService.agregarMediaALista(listaId, mediaId, usuarioLogueado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Ítem añadido a la lista exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al añadir el ítem a la lista.");
            // logger.error("Error al agregar media a lista", e); // Si tuvieras un logger
        }
        return "redirect:/listas/" + listaId; // Siempre redirige de vuelta al detalle de la lista
    }

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
            // El servicio quitarMediaDeLista ya verifica si el usuario es propietario
            listaService.quitarMediaDeLista(listaId, mediaId, usuarioLogueado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Ítem quitado de la lista exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al quitar el ítem de la lista.");
            // logger.error("Error al quitar media de lista", e); // Si tuvieras un logger
        }
        return "redirect:/listas/" + listaId; // Siempre redirige de vuelta al detalle de la lista
    }
}
