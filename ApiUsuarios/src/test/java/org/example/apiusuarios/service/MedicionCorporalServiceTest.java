package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.MedicionCorporalDTO;
import org.example.apiusuarios.model.MedicionCorporal;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.MedicionCorporalRepository;
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
class MedicionCorporalServiceTest {

    @Mock MedicionCorporalRepository medicionRepository;
    @Mock UsuarioRepository usuarioRepository;

    @InjectMocks MedicionCorporalService service;

    private Usuario usuario;
    private MedicionCorporal medicion;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(1);
        usuario.setNombre("Ana");

        medicion = new MedicionCorporal();
        medicion.setMedicionId(1);
        medicion.setUsuario(usuario);
        medicion.setFecha(LocalDate.now());
        medicion.setPeso(70.0);
        medicion.setPorcentajeGrasa(20.0);
        medicion.setMasaMagra(56.0);
        medicion.setCintura(75.0);
        medicion.setPecho(90.0);
        medicion.setBrazo(30.0);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_happyPath_retornaDTO() {
        MedicionCorporalDTO dto = new MedicionCorporalDTO();
        dto.setUsuarioId(1);
        dto.setPeso(70.0);
        dto.setPorcentajeGrasa(20.0);
        dto.setMasaMagra(56.0);
        dto.setCintura(75.0);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(medicionRepository.existsByUsuario_UsuarioIdAndFecha(eq(1), any())).thenReturn(false);
        when(medicionRepository.save(any())).thenReturn(medicion);

        MedicionCorporalDTO result = service.save(dto);

        assertThat(result.getPeso()).isEqualTo(70.0);
        assertThat(result.getPorcentajeGrasa()).isEqualTo(20.0);
        verify(medicionRepository).save(any(MedicionCorporal.class));
    }

    @Test
    void save_sinUsuarioId_throws400() {
        MedicionCorporalDTO dto = new MedicionCorporalDTO();
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("usuarioId");
    }

    @Test
    void save_usuarioNoExiste_throws404() {
        MedicionCorporalDTO dto = new MedicionCorporalDTO();
        dto.setUsuarioId(99);
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    void save_yaExisteMedicionHoy_throws409() {
        MedicionCorporalDTO dto = new MedicionCorporalDTO();
        dto.setUsuarioId(1);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(medicionRepository.existsByUsuario_UsuarioIdAndFecha(eq(1), any())).thenReturn(true);

        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ya existe");
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_camposParciales_actualizaCorrectamente() {
        when(medicionRepository.findById(1)).thenReturn(Optional.of(medicion));
        when(medicionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MedicionCorporalDTO dto = new MedicionCorporalDTO();
        dto.setPeso(75.0);
        dto.setCintura(72.0);

        MedicionCorporalDTO result = service.update(1, dto);

        assertThat(result.getPeso()).isEqualTo(75.0);
        assertThat(result.getCintura()).isEqualTo(72.0);
    }

    @Test
    void update_noExiste_throws404() {
        when(medicionRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99, new MedicionCorporalDTO()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Medición");
    }

    // ── findByUsuario ─────────────────────────────────────────────────────────

    @Test
    void findByUsuario_retornaLista() {
        when(medicionRepository.findByUsuario_UsuarioIdOrderByFechaDesc(1)).thenReturn(List.of(medicion));
        assertThat(service.findByUsuario(1)).hasSize(1);
    }

    // ── findUltima ────────────────────────────────────────────────────────────

    @Test
    void findUltima_existeMedicion_retornaDTO() {
        when(medicionRepository.findFirstByUsuario_UsuarioIdOrderByFechaDesc(1))
                .thenReturn(Optional.of(medicion));
        MedicionCorporalDTO result = service.findUltima(1);
        assertThat(result.getMedicionId()).isEqualTo(1);
    }

    @Test
    void findUltima_sinMediciones_throws404() {
        when(medicionRepository.findFirstByUsuario_UsuarioIdOrderByFechaDesc(1))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findUltima(1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Sin mediciones");
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existe_elimina() {
        when(medicionRepository.existsById(1)).thenReturn(true);
        service.deleteById(1);
        verify(medicionRepository).deleteById(1);
    }

    @Test
    void deleteById_noExiste_throws404() {
        when(medicionRepository.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> service.deleteById(99))
                .isInstanceOf(ResponseStatusException.class);
    }
}
