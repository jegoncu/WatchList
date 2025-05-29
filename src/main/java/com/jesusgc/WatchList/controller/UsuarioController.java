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

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("hideSidebar", true);
        return "auth/registro";
    }

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

    @PostMapping("/login")
    public String loginUsuario(@RequestParam String email, @RequestParam String contrasenia, HttpSession session,
            Model model) {
        try {
            Usuario usuario = usuarioService.login(email, contrasenia);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioEsAdmin", usuario.getEsAdmin());
            session.setAttribute("usuarioLogueado", usuario);

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

    @GetMapping("/logout")
    public String logoutUsuario(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

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
                usuarioForm.getEsPublico()
            );

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
}
