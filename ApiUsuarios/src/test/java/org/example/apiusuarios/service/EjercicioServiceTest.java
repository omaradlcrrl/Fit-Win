package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.EjercicioDTO;
import org.example.apiusuarios.model.*;
import org.example.apiusuarios.repository.*;
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
class EjercicioServiceTest {

    @Mock EjercicioRepository ejercicioRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock RutinaRepository rutinaRepository;
    @Mock EjercicioGlobalRepository ejercicioGlobalRepository;
    @Mock SecurityUtils securityUtils;

    @InjectMocks EjercicioService service;

    private Usuario usuario;
    private Rutina rutina;
    private EjercicioGlobal global;
    private Ejercicio ejercicio;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(1);

        rutina = new Rutina();
        rutina.setRutinaId(1);
        rutina.setNombre("Full Body");
        rutina.setUsuario(usuario);

        global = new EjercicioGlobal();
        global.setEjercicioGlobalId(1);
        global.setNombre("Press de banca");

        ejercicio = new Ejercicio();
        ejercicio.setEjercicioId(1);
        ejercicio.setUsuario(usuario);
        ejercicio.setRutina(rutina);
        ejercicio.setEjercicioGlobal(global);
        ejercicio.setSeries(4);
        ejercicio.setRepeticionesMin(8);
        ejercicio.setRepeticionesMax(12);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_happyPath_retornaDTO() {
        EjercicioDTO dto = new EjercicioDTO();
        dto.setUsuarioId(1);
        dto.setEjercicioGlobalId(1);
        dto.setRutinaId(1);
        dto.setSeries(4);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(rutinaRepository.findById(1)).thenReturn(Optional.of(rutina));
        when(ejercicioGlobalRepository.findById(1)).thenReturn(Optional.of(global));
        when(ejercicioRepository.save(any())).thenReturn(ejercicio);

        EjercicioDTO result = service.save(dto);
        assertThat(result.getSeries()).isEqualTo(4);
        verify(ejercicioRepository).save(any(Ejercicio.class));
    }

    @Test
    void save_sinUsuarioId_throws400() {
        EjercicioDTO dto = new EjercicioDTO();
        dto.setEjercicioGlobalId(1);
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("usuarioId");
    }

    @Test
    void save_sinEjercicioGlobalId_guardaConNombrePersonalizado() {
        EjercicioDTO dto = new EjercicioDTO();
        dto.setUsuarioId(1);
        dto.setNombreEjercicio("Curl de bíceps casero");

        Ejercicio sinGlobal = new Ejercicio();
        sinGlobal.setEjercicioId(2);
        sinGlobal.setUsuario(usuario);
        sinGlobal.setNombrePersonalizado("Curl de bíceps casero");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(ejercicioRepository.save(any())).thenReturn(sinGlobal);

        EjercicioDTO result = service.save(dto);
        assertThat(result.getEjercicioId()).isEqualTo(2);
        verify(ejercicioGlobalRepository, never()).findById(any());
    }

    @Test
    void save_rutinaNoExiste_throws404() {
        EjercicioDTO dto = new EjercicioDTO();
        dto.setUsuarioId(1);
        dto.setEjercicioGlobalId(1);
        dto.setRutinaId(99);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(rutinaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Rutina");
    }

    // ── findByRutina ──────────────────────────────────────────────────────────

    @Test
    void findByRutina_retornaEjercicios() {
        when(rutinaRepository.findById(1)).thenReturn(Optional.of(rutina));
        when(ejercicioRepository.findByRutina_RutinaId(1)).thenReturn(List.of(ejercicio));
        assertThat(service.findByRutina(1)).hasSize(1);
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_existe_retornaDTO() {
        when(ejercicioRepository.findById(1)).thenReturn(Optional.of(ejercicio));
        assertThat(service.findById(1).getEjercicioId()).isEqualTo(1);
    }

    @Test
    void findById_noExiste_throws404() {
        when(ejercicioRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResponseStatusException.class);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_camposParciales_actualizaCorrectamente() {
        when(ejercicioRepository.findById(1)).thenReturn(Optional.of(ejercicio));
        when(ejercicioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EjercicioDTO dto = new EjercicioDTO();
        dto.setSeries(5);
        dto.setPesoKg(80.0);

        EjercicioDTO result = service.update(1, dto);
        assertThat(result.getSeries()).isEqualTo(5);
        assertThat(result.getPesoKg()).isEqualTo(80.0);
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existe_elimina() {
        when(ejercicioRepository.findById(1)).thenReturn(Optional.of(ejercicio));
        service.deleteById(1);
        verify(ejercicioRepository).delete(ejercicio);
    }

    @Test
    void deleteById_noExiste_throws404() {
        when(ejercicioRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteById(99))
                .isInstanceOf(ResponseStatusException.class);
    }
}
