package org.example.apiusuarios.service;

import org.example.apiusuarios.model.RefreshToken;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RefreshTokenRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${security.refresh-token.expiration-ms:2592000000}") // 30 días por defecto
    private long refreshTokenExpirationMs;

    @Autowired private RefreshTokenRepository repo;
    @Autowired private UsuarioRepository usuarioRepository;

    public RefreshToken crear(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        RefreshToken token = new RefreshToken();
        token.setUsuario(usuario);
        token.setToken(UUID.randomUUID().toString());
        token.setFechaExpiracion(Instant.now().plusMillis(refreshTokenExpirationMs));
        token.setRevocado(false);
        return repo.save(token);
    }

    public RefreshToken verificar(String token) {
        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token no válido"));
        if (rt.getRevocado() || rt.getFechaExpiracion().isBefore(Instant.now())) {
            repo.delete(rt);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado o revocado");
        }
        return rt;
    }

    @Transactional
    public void revocarPorUsuario(Integer usuarioId) {
        repo.deleteByUsuario_UsuarioId(usuarioId);
    }
}
