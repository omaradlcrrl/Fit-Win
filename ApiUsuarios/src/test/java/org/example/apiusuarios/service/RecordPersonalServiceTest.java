package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.RecordPersonalDTO;
import org.example.apiusuarios.model.EjercicioGlobal;
import org.example.apiusuarios.model.RecordPersonal;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.EjercicioGlobalRepository;
import org.example.apiusuarios.repository.RecordPersonalRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordPersonalServiceTest {

    @Mock RecordPersonalRepository repo;
    @Mock UsuarioRepository usuarioRepository;
    @Mock EjercicioGlobalRepository ejercicioGlobalRepository;

    @InjectMocks RecordPersonalService service;

    private Usuario usuario;
    private EjercicioGlobal global;
    private RecordPersonal record;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(1);

        global = new EjercicioGlobal();
        global.setEjercicioGlobalId(1);
        global.setNombre("Sentadilla");

        record = new RecordPersonal();
        record.setRecordId(1);
        record.setUsuario(usuario);
        record.setEjercicioGlobal(global);
        record.setPesoKg(140.0);
        record.setFecha(LocalDate.of(2025, 1, 10));
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_happyPath_retornaDTO() {
        RecordPersonalDTO dto = new RecordPersonalDTO();
        dto.setUsuarioId(1);
        dto.setEjercicioGlobalId(1);
        dto.setPesoKg(140.0);
        dto.setFecha(LocalDate.of(2025, 1, 10));

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(ejercicioGlobalRepository.findById(1)).thenReturn(Optional.of(global));
        when(repo.save(any())).thenReturn(record);

        RecordPersonalDTO result = service.save(dto);
        assertThat(result.getPesoKg()).isEqualTo(140.0);
        assertThat(result.getNombreEjercicio()).isEqualTo("Sentadilla");
    }

    @Test
    void save_sinUsuarioId_throws400() {
        RecordPersonalDTO dto = new RecordPersonalDTO();
        dto.setEjercicioGlobalId(1);
        dto.setPesoKg(100.0);
        dto.setFecha(LocalDate.now());
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("usuarioId");
    }

    @Test
    void save_sinEjercicioGlobalId_throws400() {
        RecordPersonalDTO dto = new RecordPersonalDTO();
        dto.setUsuarioId(1);
        dto.setPesoKg(100.0);
        dto.setFecha(LocalDate.now());
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ejercicioGlobalId");
    }

    @Test
    void save_sinPesoKg_throws400() {
        RecordPersonalDTO dto = new RecordPersonalDTO();
        dto.setUsuarioId(1);
        dto.setEjercicioGlobalId(1);
        dto.setFecha(LocalDate.now());
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("pesoKg");
    }

    @Test
    void save_sinFecha_throws400() {
        RecordPersonalDTO dto = new RecordPersonalDTO();
        dto.setUsuarioId(1);
        dto.setEjercicioGlobalId(1);
        dto.setPesoKg(100.0);
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("fecha");
    }

    // ── findByUsuario ─────────────────────────────────────────────────────────

    @Test
    void findByUsuario_retornaOrdenadosPorFecha() {
        when(repo.findByUsuario_UsuarioIdOrderByFechaDesc(1)).thenReturn(List.of(record));
        assertThat(service.findByUsuario(1)).hasSize(1);
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_existe_retornaDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(record));
        assertThat(service.findById(1).getRecordId()).isEqualTo(1);
    }

    @Test
    void findById_noExiste_throws404() {
        when(repo.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResponseStatusException.class);
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existe_elimina() {
        when(repo.existsById(1)).thenReturn(true);
        service.deleteById(1);
        verify(repo).deleteById(1);
    }

    @Test
    void deleteById_noExiste_throws404() {
        when(repo.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteById(99))
                .isInstanceOf(ResponseStatusException.class);
    }
}
