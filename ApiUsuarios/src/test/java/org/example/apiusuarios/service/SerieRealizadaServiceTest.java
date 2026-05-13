package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.SerieRealizadaDTO;
import org.example.apiusuarios.model.*;
import org.example.apiusuarios.repository.*;
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
class SerieRealizadaServiceTest {

    @Mock SerieRealizadaRepository repo;
    @Mock SesionEntrenamientoRepository sesionRepo;
    @Mock EjercicioRepository ejercicioRepository;

    @InjectMocks SerieRealizadaService service;

    private SesionEntrenamiento sesion;
    private Ejercicio ejercicio;
    private SerieRealizada serie;

    @BeforeEach
    void setUp() {
        sesion = new SesionEntrenamiento();
        sesion.setSesionId(1);

        ejercicio = new Ejercicio();
        ejercicio.setEjercicioId(1);

        serie = new SerieRealizada();
        serie.setSerieId(1);
        serie.setSesion(sesion);
        serie.setEjercicio(ejercicio);
        serie.setPesoKg(100.0);
        serie.setRepeticionesRealizadas(8);
        serie.setCompletado(true);
        serie.setOrden(1);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_happyPath_retornaDTO() {
        SerieRealizadaDTO dto = new SerieRealizadaDTO();
        dto.setSesionId(1);
        dto.setPesoKg(100.0);
        dto.setRepeticionesRealizadas(8);
        dto.setOrden(1);

        when(sesionRepo.findById(1)).thenReturn(Optional.of(sesion));
        when(repo.save(any())).thenReturn(serie);

        SerieRealizadaDTO result = service.save(dto);
        assertThat(result.getPesoKg()).isEqualTo(100.0);
        verify(repo).save(any(SerieRealizada.class));
    }

    @Test
    void save_sinSesionId_throws400() {
        SerieRealizadaDTO dto = new SerieRealizadaDTO();
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("sesionId");
    }

    @Test
    void save_sesionNoExiste_throws404() {
        SerieRealizadaDTO dto = new SerieRealizadaDTO();
        dto.setSesionId(99);
        when(sesionRepo.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Sesi");
    }

    @Test
    void save_completadoNullDefautlFalse() {
        SerieRealizadaDTO dto = new SerieRealizadaDTO();
        dto.setSesionId(1);
        dto.setCompletado(null);

        when(sesionRepo.findById(1)).thenReturn(Optional.of(sesion));
        when(repo.save(any())).thenAnswer(inv -> {
            SerieRealizada s = inv.getArgument(0);
            assertThat(s.getCompletado()).isFalse();
            return serie;
        });

        service.save(dto);
    }

    // ── findBySesion ──────────────────────────────────────────────────────────

    @Test
    void findBySesion_retornaOrdenadas() {
        when(repo.findBySesion_SesionIdOrderByOrdenAsc(1)).thenReturn(List.of(serie));
        assertThat(service.findBySesion(1)).hasSize(1);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_marcaCompletado() {
        when(repo.findById(1)).thenReturn(Optional.of(serie));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SerieRealizadaDTO dto = new SerieRealizadaDTO();
        dto.setCompletado(true);
        dto.setPesoKg(105.0);

        SerieRealizadaDTO result = service.update(1, dto);
        assertThat(result.getCompletado()).isTrue();
        assertThat(result.getPesoKg()).isEqualTo(105.0);
    }

    @Test
    void update_noExiste_throws404() {
        when(repo.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99, new SerieRealizadaDTO()))
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
