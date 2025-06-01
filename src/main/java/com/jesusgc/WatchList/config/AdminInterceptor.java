package com.jesusgc.WatchList.config;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

  private final UsuarioService usuarioService;

  public AdminInterceptor(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) throws Exception {

    System.out.println("AdminInterceptor ejecutándose para: " + request.getRequestURI());

    HttpSession session = request.getSession(false);

    // Verificar si hay sesión y usuario logueado
    if (session == null || session.getAttribute("usuarioId") == null) {
      System.out.println("No hay sesión o usuarioId es null");
      response.sendRedirect("/login?error=Se requiere autenticación para acceder al panel de administración");
      return false;
    }

    Long usuarioId = (Long) session.getAttribute("usuarioId");
    System.out.println("🔍 Verificando usuario ID: " + usuarioId);

    Usuario usuario = usuarioService.findById(usuarioId);

    // Verificar que es administrador
    if (usuario == null || !Boolean.TRUE.equals(usuario.getEsAdmin())) {
      System.out.println("Usuario no es admin: " + (usuario != null ? usuario.getEsAdmin() : "usuario null"));
      System.out.println("Redirigiendo a /acceso-denegado");
      response.sendRedirect("/acceso-denegado");
      return false;
    }

    System.out.println("Usuario admin verificado correctamente");
    return true;
  }
}
