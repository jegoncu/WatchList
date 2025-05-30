package com.jesusgc.WatchList.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landingPage(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId != null) {
            return "redirect:/inicio";
        }
        model.addAttribute("currentPage", "landing");
        model.addAttribute("pageTitle", "Bienvenido a WatchList");
        model.addAttribute("hideSidebar", true);
        model.addAttribute("hideHeader", true);
        model.addAttribute("hideFooter", true);
        return "landing";
    }

    @GetMapping("/sobre-nosotros")
    public String mostrarSobreNosotros(Model model) {
        model.addAttribute("pageTitle", "Sobre Nosotros");
        model.addAttribute("hideSidebar", true);
        model.addAttribute("currentPage", "sobre-nosotros");
        return "sobre_nosotros";
    }

    @GetMapping("/contacto")
    public String mostrarContacto(Model model) {
        model.addAttribute("pageTitle", "Contacto");
        model.addAttribute("hideSidebar", true);
        model.addAttribute("currentPage", "contacto");
        return "contacto";
    }

    @GetMapping("/legal")
    public String mostrarLegal(Model model) {
        model.addAttribute("pageTitle", "Aviso Legal");
        model.addAttribute("hideSidebar", true);
        model.addAttribute("currentPage", "legal");
        return "legal";
    }

    @GetMapping("/redes-sociales")
    public String mostrarRedesSocialesPlaceholder(Model model) {
        model.addAttribute("pageTitle", "Redes Sociales");
        model.addAttribute("hideSidebar", true); 
        model.addAttribute("currentPage", "redes-sociales");
        return "redes_sociales_placeholder";
    }
}
