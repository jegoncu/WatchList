package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.service.UsuarioService; // Importar UsuarioService
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService; // Inyectar UsuarioService

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Añade el objeto usuario de nuevo al modelo si hay errores de validación
            // para que el formulario pueda repintar los valores ingresados.
            model.addAttribute("usuario", usuario);
            return "auth/registro";
        }
        try {
            // Asumimos que el objeto Usuario del formulario tiene email, nombre, contrasenia.
            // El valor de esPublico podría venir del formulario o tener un valor por defecto.
            // Si no viene del formulario, asegúrate de establecerlo.
            // Por ejemplo, si tienes un campo checkbox llamado 'esPublico':
            // usuarioService.registrar(usuario.getEmail(), usuario.getNombre(), usuario.getContrasenia(), usuario.isEsPublico());
            // O si siempre es público por defecto al registrarse:
            usuarioService.registrar(usuario.getEmail(), usuario.getNombre(), usuario.getContrasenia(), true); // Ajusta 'esPublico' según necesidad
            return "redirect:/login?registroExitoso";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuario); // Devolver el usuario para repintar el formulario
            return "auth/registro";
        }
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin(Model model, @RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout, @RequestParam(value = "registroExitoso", required = false) String registroExitoso) {
        if (error != null) {
            // El mensaje de error ahora vendrá de la excepción o se establecerá manualmente
            model.addAttribute("error", "Credenciales incorrectas. Por favor, inténtalo de nuevo.");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente.");
        }
        if (registroExitoso != null) {
            model.addAttribute("mensaje", "¡Registro exitoso! Por favor, inicia sesión.");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String loginUsuario(@RequestParam String email, @RequestParam String contrasenia, HttpSession session, Model model) {
        try {
            Usuario usuario = usuarioService.login(email, contrasenia);
            // Establecer atributos de sesión que usa _header.html
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioEsAdmin", usuario.getEsAdmin());
            // También puedes guardar el objeto completo si lo necesitas en otras partes
            session.setAttribute("usuarioLogueado", usuario);

            if (usuario.getEsAdmin() != null && usuario.getEsAdmin()) {
                return "redirect:/admin/peliculas";
            }
            return "redirect:/inicio";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
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
        return "usuario/perfil";
    }
}