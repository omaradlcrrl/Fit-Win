package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.EjercicioGlobalDTO;
import org.example.apiusuarios.model.CategoriaEjercicio;
import org.example.apiusuarios.model.EjercicioGlobal;
import org.example.apiusuarios.model.Equipamiento;
import org.example.apiusuarios.repository.EjercicioGlobalRepository;
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
class EjercicioGlobalServiceTest {

    @Mock EjercicioGlobalRepository repo;

    @InjectMocks EjercicioGlobalService service;

    private EjercicioGlobal ejercicio;

    @BeforeEach
    void setUp() {
        ejercicio = new EjercicioGlobal();
        ejercicio.setEjercicioGlobalId(1);
        ejercicio.setNombre("Press de banca");
        ejercicio.setCategoria(CategoriaEjercicio.FUERZA);
        ejercicio.setMusculoPrimario("Pectorales");
        ejercicio.setEquipamiento(Equipamiento.BARRA);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_creaYRetornaDTO() {
        when(repo.save(any())).thenReturn(ejercicio);

        EjercicioGlobalDTO dto = new EjercicioGlobalDTO(ejercicio);
        EjercicioGlobalDTO result = service.save(dto);

        assertThat(result.getNombre()).isEqualTo("Press de banca");
        assertThat(result.getCategoria()).isEqualTo(CategoriaEjercicio.FUERZA);
        verify(repo).save(any(EjercicioGlobal.class));
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void findAll_retornaTodasLasEntidades() {
        EjercicioGlobal e2 = new EjercicioGlobal();
        e2.setEjercicioGlobalId(2);
        e2.setNombre("Sentadilla");
        e2.setCategoria(CategoriaEjercicio.FUERZA);

        when(repo.findAll()).thenReturn(List.of(ejercicio, e2));

        List<EjercicioGlobalDTO> result = service.findAll();
        assertThat(result).hasSize(2);
    }

    @Test
    void findAll_listaVacia_retornaListaVacia() {
        when(repo.findAll()).thenReturn(List.of());
        assertThat(service.findAll()).isEmpty();
    }

    // ── findByCategoria ───────────────────────────────────────────────────────

    @Test
    void findByCategoria_fuerza_retornaEjerciciosDeFuerza() {
        when(repo.findByCategoria(CategoriaEjercicio.FUERZA)).thenReturn(List.of(ejercicio));

        List<EjercicioGlobalDTO> result = service.findByCategoria(CategoriaEjercicio.FUERZA);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoria()).isEqualTo(CategoriaEjercicio.FUERZA);
    }

    @Test
    void findByCategoria_sinResultados_retornaListaVacia() {
        when(repo.findByCategoria(CategoriaEjercicio.MOVILIDAD)).thenReturn(List.of());

        List<EjercicioGlobalDTO> result = service.findByCategoria(CategoriaEjercicio.MOVILIDAD);
        assertThat(result).isEmpty();
    }

    // ── search ────────────────────────────────────────────────────────────────

    @Test
    void search_terminoCoincide_retornaEjercicios() {
        when(repo.findByNombreContainingIgnoreCase("banca")).thenReturn(List.of(ejercicio));

        List<EjercicioGlobalDTO> result = service.search("banca");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).containsIgnoringCase("banca");
    }

    @Test
    void search_terminoNoCoincide_retornaVacio() {
        when(repo.findByNombreContainingIgnoreCase("inexistente")).thenReturn(List.of());
        assertThat(service.search("inexistente")).isEmpty();
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_existe_retornaDTO() {
        when(repo.findById(1)).thenReturn(Optional.of(ejercicio));

        EjercicioGlobalDTO result = service.findById(1);
        assertThat(result.getEjercicioGlobalId()).isEqualTo(1);
    }

    @Test
    void findById_noExiste_throws404() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ejercicio");
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_modificaCampos() {
        when(repo.findById(1)).thenReturn(Optional.of(ejercicio));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EjercicioGlobalDTO dto = new EjercicioGlobalDTO();
        dto.setNombre("Press banca inclinado");
        dto.setMusculoPrimario("Pectoral superior");
        dto.setCategoria(CategoriaEjercicio.HIPERTROFIA);

        EjercicioGlobalDTO result = service.update(1, dto);

        assertThat(result.getNombre()).isEqualTo("Press banca inclinado");
        assertThat(result.getCategoria()).isEqualTo(CategoriaEjercicio.HIPERTROFIA);
    }

    @Test
    void update_noExiste_throws404() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, new EjercicioGlobalDTO()))
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
