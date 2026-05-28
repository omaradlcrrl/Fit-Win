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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${security.refresh-token.expiration-ms:2592000000}") // 30 días por defecto
    private long refreshTokenExpirationMs;

    @Autowired private RefreshTokenRepository repo;
    @Autowired private UsuarioRepository usuarioRepository;

    /**
     * Crea un refresh token nuevo.
     * El valor crudo se devuelve al cliente; en BBDD se guarda solo el hash SHA-256.
     * El objeto devuelto contiene el token crudo en el campo `token` para que el
     * controller pueda mandarlo en la respuesta — pero no se persiste así.
     */
    public RefreshToken crear(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String tokenCrudo = UUID.randomUUID().toString();
        String tokenHash = hash(tokenCrudo);

        RefreshToken entidad = new RefreshToken();
        entidad.setUsuario(usuario);
        entidad.setToken(tokenHash);
        entidad.setFechaExpiracion(Instant.now().plusMillis(refreshTokenExpirationMs));
        entidad.setRevocado(false);
        RefreshToken guardado = repo.save(entidad);

        // Devolver al caller con el token crudo (no el hash) para que viaje al cliente.
        guardado.setToken(tokenCrudo);
        return guardado;
    }

    public RefreshToken verificar(String tokenCrudo) {
        String tokenHash = hash(tokenCrudo);
        RefreshToken rt = repo.findByToken(tokenHash)
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

    private String hash(String valor) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
