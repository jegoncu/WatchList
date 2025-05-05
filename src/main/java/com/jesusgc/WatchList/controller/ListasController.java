package com.jesusgc.WatchList.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ListasController {
    @GetMapping("/listas")
    public String mostrarListas(Model model) {
        model.addAttribute("currentPage", "listas");
        return "listas"; 
    }
}
