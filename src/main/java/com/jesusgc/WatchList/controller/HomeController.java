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
        return "landing";
    }

}
