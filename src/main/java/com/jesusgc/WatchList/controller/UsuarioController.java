package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para la gestión de usuarios y autenticación.
 * Maneja registro, login, logout y operaciones del perfil de usuario.
 *
 * @author Jesús González Cuenca
 */
@Controller
public class UsuarioController {

  @Autowired
  private UsuarioService usuarioService;

  /**
   * Muestra el formulario de registro de nuevos usuarios.
   *
   * @param model Modelo para la vista
   * @return Vista del formulario de registro
   */
  @GetMapping("/registro")
  public String mostrarFormularioRegistro(Model model) {
    model.addAttribute("usuario", new Usuario());
    model.addAttribute("hideSidebar", true);
    return "auth/registro";
  }

  /**
   * Procesa el registro de un nuevo usuario.
   *
   * @param usuario Datos del usuario a registrar
   * @param result  Resultado de validación
   * @param model   Modelo para la vista
   * @return Redirección a login si es exitoso, formulario con errores si no
   */
  @PostMapping("/registro")
  public String registrarUsuario(@Valid @ModelAttribute Usuario usuario, BindingResult result, Model model) {
    if (result.hasErrors()) {
      model.addAttribute("usuario", usuario);
      model.addAttribute("hideSidebar", true);
      return "auth/registro";
    }
    try {
      usuarioService.registrar(usuario.getEmail(), usuario.getNombre(), usuario.getContrasenia(), true);
      return "redirect:/login?registroExitoso";
    } catch (IllegalArgumentException e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("usuario", usuario);
      model.addAttribute("hideSidebar", true);
      return "auth/registro";
    }
  }

  /**
   * Muestra el formulario de login con mensajes de estado.
   *
   * @param model           Modelo para la vista
   * @param error           Parámetro de error opcional
   * @param logout          Parámetro de logout opcional
   * @param registroExitoso Parámetro de registro exitoso opcional
   * @return Vista del formulario de login
   */
  @GetMapping("/login")
  public String mostrarFormularioLogin(Model model, @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "logout", required = false) String logout,
      @RequestParam(value = "registroExitoso", required = false) String registroExitoso) {
    if (error != null) {
      model.addAttribute("error", "Credenciales incorrectas. Por favor, inténtalo de nuevo.");
    }
    if (logout != null) {
      model.addAttribute("mensaje", "Has cerrado sesión exitosamente.");
    }
    if (registroExitoso != null) {
      model.addAttribute("mensaje", "¡Registro exitoso! Por favor, inicia sesión.");
    }
    model.addAttribute("hideSidebar", true);
    return "auth/login";
  }

  /**
   * Procesa el login de usuario y establece la sesión.
   *
   * @param email       Email del usuario
   * @param contrasenia Contraseña del usuario
   * @param session     Sesión HTTP
   * @param model       Modelo para la vista
   * @return Redirección según el tipo de usuario o formulario con errores
   */
  @PostMapping("/login")
  public String loginUsuario(@RequestParam String email, @RequestParam String contrasenia, HttpSession session,
      Model model) {
    try {
      Usuario usuario = usuarioService.login(email, contrasenia);
      // Establecer datos de sesión
      session.setAttribute("usuarioId", usuario.getId());
      session.setAttribute("usuarioNombre", usuario.getNombre());
      session.setAttribute("usuarioEsAdmin", usuario.getEsAdmin());
      session.setAttribute("usuarioLogueado", usuario);

      // Redirigir según el tipo de usuario
      if (usuario.getEsAdmin() != null && usuario.getEsAdmin()) {
        return "redirect:/admin/peliculas";
      }
      return "redirect:/inicio";
    } catch (IllegalArgumentException e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("hideSidebar", true);
      return "auth/login";
    }
  }

  /**
   * Cierra la sesión del usuario.
   *
   * @param session Sesión HTTP a invalidar
   * @return Redirección al login con mensaje de logout
   */
  @GetMapping("/logout")
  public String logoutUsuario(HttpSession session) {
    session.invalidate();
    return "redirect:/login?logout";
  }

  /**
   * Muestra la página de inicio para usuarios autenticados.
   *
   * @param session Sesión HTTP
   * @param model   Modelo para la vista
   * @return Vista de inicio o redirección a login si no está autenticado
   */
  @GetMapping("/inicio")
  public String mostrarInicio(HttpSession session, Model model) {
    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuarioLogueado == null) {
      return "redirect:/login";
    }
    model.addAttribute("usuario", usuarioLogueado);
    model.addAttribute("hideSidebar", true);
    return "inicio";
  }

  /**
   * Muestra el perfil del usuario autenticado.
   *
   * @param session Sesión HTTP
   * @param model   Modelo para la vista
   * @return Vista del perfil o redirección a login si no está autenticado
   */
  @GetMapping("/perfil")
  public String verPerfil(HttpSession session, Model model) {
    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuarioLogueado == null) {
      return "redirect:/login";
    }
    model.addAttribute("usuario", usuarioLogueado);
    model.addAttribute("currentPage", "perfil");
    model.addAttribute("hideSidebar", true);
    return "usuario/perfil";
  }

  /**
   * Muestra el formulario de edición del perfil de usuario.
   *
   * @param session Sesión HTTP
   * @param model   Modelo para la vista
   * @return Vista del formulario de edición o redirección a login
   */
  @GetMapping("/perfil/editar")
  public String mostrarFormularioEditarPerfil(HttpSession session, Model model) {
    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuarioLogueado == null) {
      return "redirect:/login";
    }

    // Obtener datos actualizados del usuario desde la base de datos
    Usuario usuario = usuarioService.findById(usuarioLogueado.getId());
    model.addAttribute("usuario", usuario);
    model.addAttribute("currentPage", "perfil");
    model.addAttribute("hideSidebar", true);
    return "usuario/editar-perfil";
  }

  /**
   * Procesa la actualización del perfil de usuario.
   *
   * @param usuarioForm         Formulario con los nuevos datos del usuario
   * @param result              Resultado de validación
   * @param contrasenaActual    Contraseña actual para verificación
   * @param nuevaContrasena     Nueva contraseña opcional
   * @param confirmarContrasena Confirmación de la nueva contraseña
   * @param session             Sesión HTTP
   * @param model               Modelo para la vista
   * @param redirectAttributes  Atributos para redirección
   * @return Redirección al perfil si es exitoso, formulario con errores si no
   */
  @PostMapping("/perfil/editar")
  public String actualizarPerfil(
      @Valid @ModelAttribute Usuario usuarioForm,
      BindingResult result,
      @RequestParam("contrasenaActual") String contrasenaActual,
      @RequestParam(value = "nuevaContrasena", required = false) String nuevaContrasena,
      @RequestParam(value = "confirmarContrasena", required = false) String confirmarContrasena,
      HttpSession session,
      Model model,
      RedirectAttributes redirectAttributes) {

    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuarioLogueado == null) {
      return "redirect:/login";
    }

    // Verificar contraseña actual
    try {
      Usuario usuarioActual = usuarioService.findById(usuarioLogueado.getId());
      if (!org.mindrot.jbcrypt.BCrypt.checkpw(contrasenaActual, usuarioActual.getContrasenia())) {
        model.addAttribute("errorMessage", "La contraseña actual es incorrecta");
        model.addAttribute("usuario", usuarioForm);
        model.addAttribute("currentPage", "perfil");
        model.addAttribute("hideSidebar", true);
        return "usuario/editar-perfil";
      }
    } catch (Exception e) {
      model.addAttribute("errorMessage", "Error al verificar la contraseña");
      model.addAttribute("usuario", usuarioForm);
      model.addAttribute("currentPage", "perfil");
      model.addAttribute("hideSidebar", true);
      return "usuario/editar-perfil";
    }

    // Validar nueva contraseña si se proporciona
    if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
      if (nuevaContrasena.length() < 6) {
        model.addAttribute("errorMessage", "La nueva contraseña debe tener al menos 6 caracteres");
        model.addAttribute("usuario", usuarioForm);
        model.addAttribute("currentPage", "perfil");
        model.addAttribute("hideSidebar", true);
        return "usuario/editar-perfil";
      }
      if (!nuevaContrasena.equals(confirmarContrasena)) {
        model.addAttribute("errorMessage", "Las contraseñas nuevas no coinciden");
        model.addAttribute("usuario", usuarioForm);
        model.addAttribute("currentPage", "perfil");
        model.addAttribute("hideSidebar", true);
        return "usuario/editar-perfil";
      }
    }

    // Validar errores de campos básicos
    if (result.hasFieldErrors("nombre") || result.hasFieldErrors("email")) {
      model.addAttribute("usuario", usuarioForm);
      model.addAttribute("currentPage", "perfil");
      model.addAttribute("hideSidebar", true);
      return "usuario/editar-perfil";
    }

    try {
      Usuario usuarioActualizado = usuarioService.actualizarPerfil(
          usuarioLogueado.getId(),
          usuarioForm.getNombre(),
          usuarioForm.getEmail(),
          contrasenaActual,
          nuevaContrasena,
          confirmarContrasena,
          usuarioForm.getEsPublico());

      // Actualizar el usuario en la sesión
      session.setAttribute("usuarioLogueado", usuarioActualizado);
      session.setAttribute("usuarioNombre", usuarioActualizado.getNombre());

      redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado correctamente.");
      return "redirect:/perfil";

    } catch (Exception e) {
      model.addAttribute("errorMessage", e.getMessage());
      model.addAttribute("usuario", usuarioForm);
      model.addAttribute("currentPage", "perfil");
      model.addAttribute("hideSidebar", true);
      return "usuario/editar-perfil";
    }
  }

  /**
   * Elimina la cuenta del usuario autenticado.
   *
   * @param session            Sesión HTTP
   * @param redirectAttributes Atributos para redirección
   * @return Redirección al login con mensaje de confirmación o al perfil con
   *         error
   */
  @PostMapping("/perfil/eliminar")
  public String eliminarCuenta(HttpSession session, RedirectAttributes redirectAttributes) {
    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
    if (usuarioLogueado == null) {
      return "redirect:/login";
    }

    try {
      usuarioService.deleteById(usuarioLogueado.getId());
      session.invalidate(); // Cerrar sesión
      redirectAttributes.addFlashAttribute("successMessage",
          "Tu cuenta ha sido eliminada correctamente. Lamentamos verte partir.");
      return "redirect:/login";

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorMessage",
          "Error al eliminar la cuenta: " + e.getMessage());
      return "redirect:/perfil";
    }
  }

  /**
   * Muestra la página de acceso denegado.
   * Se activa cuando un usuario sin permisos intenta acceder a rutas
   * administrativas.
   *
   * @param model Modelo para la vista
   * @return Vista de acceso denegado
   */
  @GetMapping("/acceso-denegado")
  public String mostrarAccesoDenegado(Model model) {
    System.out.println("Mostrando página de acceso denegado");

    model.addAttribute("pageTitle", "Acceso Denegado");
    model.addAttribute("hideSidebar", true); // Opcional: ocultar sidebar

    return "acceso-denegado";
  }

}
