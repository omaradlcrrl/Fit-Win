package org.example.apiusuarios.service;

import org.example.apiusuarios.model.RefreshToken;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RefreshTokenRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock RefreshTokenRepository repo;
    @Mock UsuarioRepository usuarioRepository;

    @InjectMocks RefreshTokenService refreshTokenService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Inyectar valor de @Value manualmente
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationMs", 2592000000L);

        usuario = new Usuario();
        usuario.setUsuarioId(1);
        usuario.setCorreoElectronico("test@example.com");
    }

    // ── crear ─────────────────────────────────────────────────────────────────

    @Test
    void crear_usuarioExiste_creaTokenYLoGuarda() {
        RefreshToken savedToken = new RefreshToken();
        savedToken.setTokenId(1);
        savedToken.setUsuario(usuario);
        savedToken.setRevocado(false);
        savedToken.setFechaExpiracion(Instant.now().plusMillis(2592000000L));

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(repo.save(any(RefreshToken.class))).thenReturn(savedToken);

        RefreshToken result = refreshTokenService.crear(1);

        assertThat(result).isNotNull();
        assertThat(result.getRevocado()).isFalse();
        assertThat(result.getFechaExpiracion()).isAfter(Instant.now());
        verify(repo).save(any(RefreshToken.class));
    }

    @Test
    void crear_usuarioNoExiste_throws404() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.crear(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    void crear_tokenEsUUID_formato36Chars() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(repo.save(any(RefreshToken.class))).thenAnswer(inv -> {
            RefreshToken t = inv.getArgument(0);
            t.setTokenId(1);
            return t;
        });

        RefreshToken result = refreshTokenService.crear(1);
        // UUID tiene formato 8-4-4-4-12 = 36 caracteres
        assertThat(result.getToken()).hasSize(36);
    }

    // ── verificar ─────────────────────────────────────────────────────────────

    @Test
    void verificar_tokenValido_retornaToken() {
        RefreshToken token = new RefreshToken();
        token.setToken("valid-token");
        token.setRevocado(false);
        token.setFechaExpiracion(Instant.now().plusSeconds(3600));

        when(repo.findByToken("valid-token")).thenReturn(Optional.of(token));

        RefreshToken result = refreshTokenService.verificar("valid-token");
        assertThat(result.getToken()).isEqualTo("valid-token");
    }

    @Test
    void verificar_tokenNoExiste_throws401() {
        when(repo.findByToken("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.verificar("inexistente"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("válido");
    }

    @Test
    void verificar_tokenRevocado_borraYThrows401() {
        RefreshToken token = new RefreshToken();
        token.setToken("revoked-token");
        token.setRevocado(true);
        token.setFechaExpiracion(Instant.now().plusSeconds(3600));

        when(repo.findByToken("revoked-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.verificar("revoked-token"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("expirado");

        verify(repo).delete(token);
    }

    @Test
    void verificar_tokenExpirado_borraYThrows401() {
        RefreshToken token = new RefreshToken();
        token.setToken("expired-token");
        token.setRevocado(false);
        token.setFechaExpiracion(Instant.now().minusSeconds(1)); // ya expiró

        when(repo.findByToken("expired-token")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> refreshTokenService.verificar("expired-token"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("expirado");

        verify(repo).delete(token);
    }

    @Test
    void verificar_tokenExpiraEnExactoUnSegundo_esValido() {
        RefreshToken token = new RefreshToken();
        token.setToken("barely-valid");
        token.setRevocado(false);
        token.setFechaExpiracion(Instant.now().plusSeconds(1));

        when(repo.findByToken("barely-valid")).thenReturn(Optional.of(token));

        assertThatCode(() -> refreshTokenService.verificar("barely-valid"))
                .doesNotThrowAnyException();
    }

    // ── revocarPorUsuario ─────────────────────────────────────────────────────

    @Test
    void revocarPorUsuario_llamaDeleteEnRepo() {
        refreshTokenService.revocarPorUsuario(1);
        verify(repo).deleteByUsuario_UsuarioId(1);
    }
}
