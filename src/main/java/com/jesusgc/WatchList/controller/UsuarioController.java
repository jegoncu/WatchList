package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login"; 
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "auth/registro"; 
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String email,
            @RequestParam String nombre,
            @RequestParam String contrasenia,
            @RequestParam(defaultValue = "true") boolean esPublico,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.registrar(email, nombre, contrasenia, esPublico);
            redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso. Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("nombre", nombre);
            return "redirect:/registro";
        }
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String email,
            @RequestParam String contrasenia,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioService.login(email, contrasenia);

            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioEsAdmin", usuario.getEsAdmin());

            return "redirect:/inicio";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.findById(usuarioId);

        if (usuario == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("currentPage", "inicio");
        return "inicio";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            session.invalidate();
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("currentPage", "perfil");
        return "usuario/perfil"; 
    }
    
    @GetMapping("/mis-listas")
    public String mostrarMisListas(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            session.invalidate();
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("currentPage", "mis-listas");
        return "usuario/mis-listas"; 
    }
}