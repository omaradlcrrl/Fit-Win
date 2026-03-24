package org.example.apiusuarios.security;

import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));

        // Convertir el Role a Authority de Spring Security
        // Spring espera el prefijo "ROLE_" para métodos como hasRole()
        // Validar si el rol es null (usuarios antiguos)
        String roleName = (usuario.getRole() != null) ? usuario.getRole().name() : "USER";

        var authority = new org.springframework.security.core.authority.SimpleGrantedAuthority(
                "ROLE_" + roleName);

        return new User(
                usuario.getCorreoElectronico(),
                usuario.getPassword(),
                java.util.Collections.singletonList(authority));
    }
}
