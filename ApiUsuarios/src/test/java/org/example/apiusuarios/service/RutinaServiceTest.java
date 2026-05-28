package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.RutinaDTO;
import org.example.apiusuarios.model.Rutina;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RutinaRepository;
import org.example.apiusuarios.repository.SerieRealizadaRepository;
import org.example.apiusuarios.repository.SesionEntrenamientoRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.example.apiusuarios.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RutinaServiceTest {

    @Mock RutinaRepository rutinaRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock SesionEntrenamientoRepository sesionRepository;
    @Mock SerieRealizadaRepository serieRealizadaRepository;
    @Mock SecurityUtils securityUtils;

    @InjectMocks RutinaService service;

    private Usuario usuario;
    private Rutina rutina;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(1);

        rutina = new Rutina();
        rutina.setRutinaId(1);
        rutina.setNombre("Full Body");
        rutina.setUsuario(usuario);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_happyPath_retornaDTO() {
        RutinaDTO dto = new RutinaDTO();
        dto.setUsuarioId(1);
        dto.setNombre("Full Body");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(rutinaRepository.save(any())).thenReturn(rutina);

        RutinaDTO result = service.save(dto);
        assertThat(result.getNombre()).isEqualTo("Full Body");
        verify(rutinaRepository).save(any(Rutina.class));
    }

    @Test
    void save_sinUsuarioId_throws400() {
        RutinaDTO dto = new RutinaDTO();
        dto.setNombre("Full Body");
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("usuarioId");
    }

    @Test
    void save_sinNombre_throws400() {
        RutinaDTO dto = new RutinaDTO();
        dto.setUsuarioId(1);
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void save_usuarioNoExiste_throws404() {
        RutinaDTO dto = new RutinaDTO();
        dto.setUsuarioId(99);
        dto.setNombre("Full Body");
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario");
    }

    // ── findByUsuario ─────────────────────────────────────────────────────────

    @Test
    void findByUsuario_retornaLista() {
        when(rutinaRepository.findByUsuario_UsuarioId(1)).thenReturn(List.of(rutina));
        assertThat(service.findByUsuario(1)).hasSize(1);
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_existe_retornaDTO() {
        when(rutinaRepository.findById(1)).thenReturn(Optional.of(rutina));
        assertThat(service.findById(1).getNombre()).isEqualTo("Full Body");
    }

    @Test
    void findById_noExiste_throws404() {
        when(rutinaRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Rutina");
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_camposOpcionales_actualizaCorrectamente() {
        when(rutinaRepository.findById(1)).thenReturn(Optional.of(rutina));
        when(rutinaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RutinaDTO dto = new RutinaDTO();
        dto.setNombre("Push Pull Legs");
        dto.setDiasActivos("L,M,X,J,V");

        RutinaDTO result = service.update(1, dto);
        assertThat(result.getNombre()).isEqualTo("Push Pull Legs");
        assertThat(result.getDiasActivos()).isEqualTo("L,M,X,J,V");
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existe_elimina() {
        when(rutinaRepository.findById(1)).thenReturn(Optional.of(rutina));
        when(sesionRepository.findByRutina_RutinaId(1)).thenReturn(List.of());
        service.deleteById(1);
        verify(rutinaRepository).deleteById(1);
    }

    @Test
    void deleteById_noExiste_throws404() {
        when(rutinaRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteById(99))
                .isInstanceOf(ResponseStatusException.class);
    }
}
