package com.jesusgc.WatchList.controller;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Usuario usuario = usuarioService.findById(usuarioId);
            // No devolvemos la contraseña por seguridad
            usuario.setContrasenia(null);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Map<String, Object> datos) {
        try {
            String email = (String) datos.get("email");
            String nombre = (String) datos.get("nombre");
            String contrasenia = (String) datos.get("contrasenia");
            boolean esPublico = datos.containsKey("esPublico") ? (boolean) datos.get("esPublico") : true;
            
            Usuario usuario = usuarioService.registrar(email, nombre, contrasenia, esPublico);
            
            // No devolvemos la contraseña por seguridad
            usuario.setContrasenia(null);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos, HttpSession session) {
        try {
            String email = datos.get("email");
            String contrasenia = datos.get("contrasenia");
            
            if (email == null || contrasenia == null) {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "Email y contraseña son requeridos");
                return ResponseEntity.badRequest().body(error);
            }
            
            Usuario usuario = usuarioService.login(email, contrasenia);
            
            // Almacenar datos en la sesion
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioEmail", usuario.getEmail());
            session.setAttribute("usuarioEsAdmin", usuario.getEsAdmin());
            
            // No devolvemos la contraseña por seguridad
            usuario.setContrasenia(null);
            
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Sesión cerrada correctamente");
        return ResponseEntity.ok(response);
    }
}