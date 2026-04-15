package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.ComidaDTO;
import org.example.apiusuarios.model.Comida;
import org.example.apiusuarios.model.TipoComida;
import org.example.apiusuarios.model.UnidadComida;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.ComidaRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class ComidaServiceTest {

    @Mock ComidaRepository comidaRepository;
    @Mock UsuarioRepository usuarioRepository;

    @InjectMocks ComidaService comidaService;

    private Usuario usuario;
    private Comida comidaBase;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(1);
        usuario.setNombre("Test");

        comidaBase = new Comida();
        comidaBase.setComidaId(1);
        comidaBase.setNombre("Pollo asado");
        comidaBase.setCalorias(250);
        comidaBase.setProteinas(30.0);
        comidaBase.setCarbohidratos(5.0);
        comidaBase.setGrasasSaturadas(4.0);
        comidaBase.setTipoComida(TipoComida.ALMUERZO);
        comidaBase.setCantidad(200.0);
        comidaBase.setUnidad(UnidadComida.GRAMOS);
        comidaBase.setFecha(LocalDate.now());
        comidaBase.setUsuario(usuario);
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_happyPath_guardaYRetornaDTO() {
        ComidaDTO dto = buildDTO();
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(comidaRepository.save(any())).thenReturn(comidaBase);

        ComidaDTO result = comidaService.save(dto);

        assertThat(result.getNombre()).isEqualTo("Pollo asado");
        assertThat(result.getTipoComida()).isEqualTo(TipoComida.ALMUERZO);
        assertThat(result.getCantidad()).isEqualTo(200.0);
        assertThat(result.getUnidad()).isEqualTo(UnidadComida.GRAMOS);
    }

    @Test
    void save_mapeoTipoComida_persiste() {
        ComidaDTO dto = buildDTO();
        dto.setTipoComida(TipoComida.DESAYUNO);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(comidaRepository.save(any())).thenAnswer(inv -> {
            Comida c = inv.getArgument(0);
            c.setComidaId(1);
            c.setUsuario(usuario);
            return c;
        });

        comidaService.save(dto);

        ArgumentCaptor<Comida> captor = ArgumentCaptor.forClass(Comida.class);
        verify(comidaRepository).save(captor.capture());
        assertThat(captor.getValue().getTipoComida()).isEqualTo(TipoComida.DESAYUNO);
    }

    @Test
    void save_mapeoUnidadML_persiste() {
        ComidaDTO dto = buildDTO();
        dto.setUnidad(UnidadComida.ML);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(comidaRepository.save(any())).thenAnswer(inv -> {
            Comida c = inv.getArgument(0);
            c.setComidaId(1);
            c.setUsuario(usuario);
            return c;
        });

        comidaService.save(dto);

        ArgumentCaptor<Comida> captor = ArgumentCaptor.forClass(Comida.class);
        verify(comidaRepository).save(captor.capture());
        assertThat(captor.getValue().getUnidad()).isEqualTo(UnidadComida.ML);
    }

    @Test
    void save_nullUsuarioId_throws400() {
        ComidaDTO dto = buildDTO();
        dto.setUsuarioId(null);

        assertThatThrownBy(() -> comidaService.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("usuario");
    }

    @Test
    void save_usuarioNoExiste_throws404() {
        ComidaDTO dto = buildDTO();
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> comidaService.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario");
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_actualizaNuevosCampos() {
        when(comidaRepository.findById(1)).thenReturn(Optional.of(comidaBase));
        when(comidaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ComidaDTO dto = new ComidaDTO();
        dto.setTipoComida(TipoComida.CENA);
        dto.setCantidad(150.0);
        dto.setUnidad(UnidadComida.UNIDAD);

        ComidaDTO result = comidaService.update(1, dto);

        assertThat(result.getTipoComida()).isEqualTo(TipoComida.CENA);
        assertThat(result.getCantidad()).isEqualTo(150.0);
        assertThat(result.getUnidad()).isEqualTo(UnidadComida.UNIDAD);
    }

    @Test
    void update_soloActualizaCamposNoNull() {
        when(comidaRepository.findById(1)).thenReturn(Optional.of(comidaBase));
        when(comidaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ComidaDTO dto = new ComidaDTO();
        dto.setNombre("Arroz blanco");
        // tipoComida, cantidad y unidad son null → no deben cambiar

        ComidaDTO result = comidaService.update(1, dto);

        assertThat(result.getNombre()).isEqualTo("Arroz blanco");
        assertThat(result.getTipoComida()).isEqualTo(TipoComida.ALMUERZO); // no cambiado
        assertThat(result.getCantidad()).isEqualTo(200.0);                 // no cambiado
    }

    @Test
    void update_comidaNoExiste_throws404() {
        when(comidaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> comidaService.update(99, new ComidaDTO()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Comida");
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_existe_retornaDTO() {
        when(comidaRepository.findById(1)).thenReturn(Optional.of(comidaBase));
        ComidaDTO result = comidaService.findById(1);
        assertThat(result.getComidaId()).isEqualTo(1);
    }

    @Test
    void findById_noExiste_throws404() {
        when(comidaRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> comidaService.findById(99))
                .isInstanceOf(ResponseStatusException.class);
    }

    // ── findByUsuarioHoy ──────────────────────────────────────────────────────

    @Test
    void findByUsuarioHoy_retornaCodimasDeHoy() {
        when(comidaRepository.findByUsuario_UsuarioIdAndFecha(eq(1), any(LocalDate.class)))
                .thenReturn(List.of(comidaBase));

        List<ComidaDTO> result = comidaService.findByUsuarioHoy(1);
        assertThat(result).hasSize(1);
    }

    @Test
    void findByUsuarioHoy_nullUsuarioId_throws400() {
        assertThatThrownBy(() -> comidaService.findByUsuarioHoy(null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("usuarioId");
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existe_elimina() {
        when(comidaRepository.existsById(1)).thenReturn(true);
        comidaService.deleteById(1);
        verify(comidaRepository).deleteById(1);
    }

    @Test
    void deleteById_noExiste_throws404() {
        when(comidaRepository.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> comidaService.deleteById(99))
                .isInstanceOf(ResponseStatusException.class);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private ComidaDTO buildDTO() {
        ComidaDTO dto = new ComidaDTO();
        dto.setNombre("Pollo asado");
        dto.setCalorias(250);
        dto.setProteinas(30.0);
        dto.setCarbohidratos(5.0);
        dto.setGrasasSaturadas(4.0);
        dto.setTipoComida(TipoComida.ALMUERZO);
        dto.setCantidad(200.0);
        dto.setUnidad(UnidadComida.GRAMOS);
        dto.setFecha(LocalDate.now());
        dto.setUsuarioId(1);
        return dto;
    }
}
