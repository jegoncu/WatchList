package com.jesusgc.WatchList.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class SeriesController {
    @GetMapping("/series")
    public String mostrarSeries(Model model) {
        model.addAttribute("currentPage", "series");
        return "series"; 
    }
}
