package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Usuario registrar(String email, String nombre, String contrasenia, boolean esPublico) {
        // Comprobar si el email ya existe
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNombre(nombre);
        usuario.setContrasenia(passwordEncoder.encode(contrasenia));
        usuario.setEsPublico(esPublico);
        usuario.setEsAdmin(false); // Por defecto no es admin
        
        return usuarioRepository.save(usuario);
    }
    
    public Usuario login(String email, String contrasenia) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Email o contraseña incorrectos");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!passwordEncoder.matches(contrasenia, usuario.getContrasenia())) {
            throw new IllegalArgumentException("Email o contraseña incorrectos");
        }
        
        return usuario;
    }
    
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}