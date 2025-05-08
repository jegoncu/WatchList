package com.jesusgc.WatchList.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class GenteController {
    @GetMapping("/gente")
    public String mostrarGente(Model model) {
        model.addAttribute("currentPage", "gente");
        return "gente";
    }
}
