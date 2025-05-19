package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt; 

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrar(String email, String nombre, String contrasenia, boolean esPublico) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNombre(nombre);


        String hashedPassword = BCrypt.hashpw(contrasenia, BCrypt.gensalt());
        usuario.setContrasenia(hashedPassword);

        usuario.setEsPublico(esPublico);
        usuario.setEsAdmin(false);

        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String contrasenia) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Email o contraseña incorrectos");
        }

        Usuario usuario = usuarioOpt.get();
        if (!BCrypt.checkpw(contrasenia, usuario.getContrasenia())) {
            throw new IllegalArgumentException("Email o contraseña incorrectos");
        }

        return usuario;
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}