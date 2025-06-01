package com.jesusgc.WatchList.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para páginas principales y de información del sitio.
 * Maneja la página de inicio, información corporativa y páginas legales.
 *
 * @author Jesús González Cuenca
 */
@Controller
public class HomeController {

  /**
   * Muestra la página de bienvenida o redirige al inicio si el usuario está
   * autenticado.
   *
   * @param session Sesión HTTP para verificar autenticación
   * @param model   Modelo para la vista
   * @return Vista de landing page o redirección al inicio
   */
  @GetMapping("/")
  public String landingPage(HttpSession session, Model model) {
    // Long usuarioId = (Long) session.getAttribute("usuarioId");

    // Redirigir a inicio si el usuario ya está autenticado
    // if (usuarioId != null) {
    //   return "redirect:/inicio";
    // }
    model.addAttribute("currentPage", "landing");
    model.addAttribute("pageTitle", "Bienvenido a WatchList");
    model.addAttribute("hideSidebar", true);
    model.addAttribute("hideHeader", true);
    model.addAttribute("hideFooter", true);
    return "landing";
  }

  /**
   * Muestra la página de información "Sobre Nosotros".
   *
   * @param model Modelo para la vista
   * @return Vista de información sobre la empresa
   */
  @GetMapping("/sobre-nosotros")
  public String mostrarSobreNosotros(Model model) {
    model.addAttribute("pageTitle", "Sobre Nosotros");
    model.addAttribute("hideSidebar", true);
    model.addAttribute("currentPage", "sobre-nosotros");
    return "sobre_nosotros";
  }

  /**
   * Muestra la página de contacto.
   *
   * @param model Modelo para la vista
   * @return Vista de información de contacto
   */
  @GetMapping("/contacto")
  public String mostrarContacto(Model model) {
    model.addAttribute("pageTitle", "Contacto");
    model.addAttribute("hideSidebar", true);
    model.addAttribute("currentPage", "contacto");
    return "contacto";
  }

  /**
   * Muestra la página de aviso legal.
   *
   * @param model Modelo para la vista
   * @return Vista del aviso legal
   */
  @GetMapping("/legal")
  public String mostrarLegal(Model model) {
    model.addAttribute("pageTitle", "Aviso Legal");
    model.addAttribute("hideSidebar", true);
    model.addAttribute("currentPage", "legal");
    return "legal";
  }

  /**
   * Muestra página de placeholder para redes sociales.
   *
   * @param model Modelo para la vista
   * @return Vista de placeholder de redes sociales
   */
  @GetMapping("/redes-sociales")
  public String mostrarRedesSocialesPlaceholder(Model model) {
    model.addAttribute("pageTitle", "Redes Sociales");
    model.addAttribute("hideSidebar", true);
    model.addAttribute("currentPage", "redes-sociales");
    return "redes_sociales_placeholder";
  }
}
