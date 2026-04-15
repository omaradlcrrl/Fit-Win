package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.UsuarioDTO;
import org.example.apiusuarios.dto.login.LoginRequest;
import org.example.apiusuarios.dto.login.LoginResponse;
import org.example.apiusuarios.exception.CredencialesInvalidasException;
import org.example.apiusuarios.exception.RecursoNoEncontradoException;
import org.example.apiusuarios.model.Role;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.example.apiusuarios.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository usuarioRepository;
    @Mock AuthenticationManager authenticationManager;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock UserDetailsService userDetailsService;

    @InjectMocks UsuarioService usuarioService;

    private UsuarioDTO dtoBase;
    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        dtoBase = new UsuarioDTO();
        dtoBase.setNombre("Ana");
        dtoBase.setApellidos("Lopez");
        dtoBase.setCorreoElectronico("ana@example.com");
        dtoBase.setPassword("password123");
        dtoBase.setGenero("FEMENINO");
        dtoBase.setNivelActividad("MODERADO");
        dtoBase.setEstrategia("MANTENIMIENTO");
        dtoBase.setAjusteCalorico(10);

        usuarioBase = new Usuario();
        usuarioBase.setUsuarioId(1);
        usuarioBase.setNombre("Ana");
        usuarioBase.setCorreoElectronico("ana@example.com");
        usuarioBase.setPassword("encoded");
        usuarioBase.setGenero("FEMENINO");
        usuarioBase.setNivelActividad("MODERADO");
        usuarioBase.setRole(Role.USER);
    }

    // ── save ────────────────────────────────────────────────────────────────

    @Test
    void save_happyPath_returnsDTO() {
        when(usuarioRepository.findByCorreoElectronico("ana@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenReturn(usuarioBase);

        UsuarioDTO result = usuarioService.save(dtoBase);

        assertThat(result.getCorreoElectronico()).isEqualTo("ana@example.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void save_normalizesEmailToLowercase() {
        dtoBase.setCorreoElectronico("ANA@EXAMPLE.COM");
        when(usuarioRepository.findByCorreoElectronico("ana@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenReturn(usuarioBase);

        usuarioService.save(dtoBase);

        verify(usuarioRepository).findByCorreoElectronico("ana@example.com");
    }

    @Test
    void save_blankEmail_throws400() {
        dtoBase.setCorreoElectronico("   ");
        assertThatThrownBy(() -> usuarioService.save(dtoBase))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("correo");
    }

    @Test
    void save_invalidEmailFormat_throws400() {
        dtoBase.setCorreoElectronico("not-an-email");
        assertThatThrownBy(() -> usuarioService.save(dtoBase))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("correo");
    }

    @Test
    void save_duplicateEmail_throws409() {
        when(usuarioRepository.findByCorreoElectronico("ana@example.com")).thenReturn(Optional.of(usuarioBase));
        assertThatThrownBy(() -> usuarioService.save(dtoBase))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("registrado");
    }

    @Test
    void save_passwordTooShort_throws400() {
        dtoBase.setPassword("abc");
        when(usuarioRepository.findByCorreoElectronico(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.save(dtoBase))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("contraseña");
    }

    @Test
    void save_nullGenero_throws400() {
        dtoBase.setGenero(null);
        when(usuarioRepository.findByCorreoElectronico(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.save(dtoBase))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("género");
    }

    @Test
    void save_invalidEstrategia_throws400() {
        dtoBase.setEstrategia("INVALIDA");
        when(usuarioRepository.findByCorreoElectronico(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.save(dtoBase))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Estrategia");
    }

    @Test
    void save_ajusteCaloricoOutOfRange_throws400() {
        dtoBase.setAjusteCalorico(99);
        when(usuarioRepository.findByCorreoElectronico(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.save(dtoBase))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ajusteCalorico");
    }

    // ── login ────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsTokenAndId() {
        LoginRequest req = new LoginRequest();
        req.setCorreoElectronico("ana@example.com");
        req.setPassword("password123");

        UserDetails ud = mock(UserDetails.class);

        when(usuarioRepository.findByCorreoElectronico("ana@example.com")).thenReturn(Optional.of(usuarioBase));
        when(userDetailsService.loadUserByUsername("ana@example.com")).thenReturn(ud);
        when(jwtService.generateToken(ud)).thenReturn("jwt.token.here");

        LoginResponse resp = usuarioService.login(req);

        assertThat(resp.getToken()).isEqualTo("jwt.token.here");
        assertThat(resp.getUsuarioId()).isEqualTo(1);
    }

    @Test
    void login_badCredentials_throwsCredencialesInvalidas() {
        LoginRequest req = new LoginRequest();
        req.setCorreoElectronico("ana@example.com");
        req.setPassword("wrong");

        doThrow(new BadCredentialsException("bad")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> usuarioService.login(req))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_exists_returnsDTO() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioBase));
        UsuarioDTO dto = usuarioService.findById(1);
        assertThat(dto.getUsuarioId()).isEqualTo(1);
    }

    @Test
    void findById_notFound_throwsRecursoNoEncontrado() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.findById(99))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_validFields_savesAndReturns() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.save(any())).thenReturn(usuarioBase);

        UsuarioDTO updates = new UsuarioDTO();
        updates.setNombre("Nuevo");

        UsuarioDTO result = usuarioService.update(1, updates);
        assertThat(result).isNotNull();
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void update_invalidEmail_throws400() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioBase));
        UsuarioDTO updates = new UsuarioDTO();
        updates.setCorreoElectronico("bad-email");

        assertThatThrownBy(() -> usuarioService.update(1, updates))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("correo");
    }

    @Test
    void update_shortPassword_throws400() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioBase));
        UsuarioDTO updates = new UsuarioDTO();
        updates.setPassword("abc");

        assertThatThrownBy(() -> usuarioService.update(1, updates))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("contraseña");
    }
}
