package org.example.apiusuarios.security;

import org.example.apiusuarios.exception.RecursoNoEncontradoException;
import org.example.apiusuarios.model.Role;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("No autenticado");
        }
        String correo = auth.getName();
        return usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario autenticado no existe en BBDD"));
    }

    public Integer getUsuarioActualId() {
        return getUsuarioActual().getUsuarioId();
    }

    public boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.ADMIN.name()));
    }

    public void assertEsDuenoOAdmin(Integer usuarioId) {
        if (usuarioId == null) {
            throw new AccessDeniedException("Falta usuarioId");
        }
        if (esAdmin()) return;
        Integer actual = getUsuarioActualId();
        if (!actual.equals(usuarioId)) {
            throw new AccessDeniedException("No tienes permiso sobre este recurso");
        }
    }
}
