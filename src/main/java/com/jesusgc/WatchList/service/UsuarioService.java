package com.jesusgc.WatchList.service;

import com.jesusgc.WatchList.model.Usuario;
import com.jesusgc.WatchList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrar(String email, String nombre, String contrasenia, boolean esPublico) {
        return registrar(email, nombre, contrasenia, esPublico, false);
    }

    @Transactional
    public Usuario registrar(String email, String nombre, String contrasenia, boolean esPublico, boolean esAdmin) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setNombre(nombre);

        String hashedPassword = BCrypt.hashpw(contrasenia, BCrypt.gensalt());
        usuario.setContrasenia(hashedPassword);

        usuario.setEsPublico(esPublico);
        usuario.setEsAdmin(esAdmin);

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

    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByIdOptional(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public Usuario updateUsuario(Long id, String nombre, String email, String nuevaContrasenia,
                                 Boolean esPublico, Boolean esAdmin) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado por otro usuario");
        }

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setEsPublico(esPublico);
        usuario.setEsAdmin(esAdmin);

        if (nuevaContrasenia != null && !nuevaContrasenia.trim().isEmpty()) {
            String hashedPassword = BCrypt.hashpw(nuevaContrasenia, BCrypt.gensalt());
            usuario.setContrasenia(hashedPassword);
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarPerfil(Long usuarioId, String nombre, String email,
                                   String contrasenaActual, String nuevaContrasena,
                                   String confirmarContrasena, Boolean esPublico) {

        Usuario usuario = findById(usuarioId);

        if (!BCrypt.checkpw(contrasenaActual, usuario.getContrasenia())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        if (!usuario.getEmail().equals(email) && usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado por otro usuario");
        }

        if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
            if (nuevaContrasena.length() < 6) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
            }
            if (!nuevaContrasena.equals(confirmarContrasena)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden");
            }
            String hashedPassword = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt());
            usuario.setContrasenia(hashedPassword);
        }

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setEsPublico(esPublico);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarCuentaPropia(Long usuarioId, String contrasenaConfirmacion) {
        Usuario usuario = findById(usuarioId);

        if (!BCrypt.checkpw(contrasenaConfirmacion, usuario.getContrasenia())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        usuarioRepository.delete(usuario);
    }
}
