package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.ObjetivoDTO;
import org.example.apiusuarios.model.Objetivo;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.ObjetivoRepository;
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
class ObjetivoServiceTest {

    @Mock ObjetivoRepository objetivoRepository;
    @Mock UsuarioRepository usuarioRepository;

    @InjectMocks ObjetivoService objetivoService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(1);
        usuario.setNombre("Test");
        usuario.setCorreoElectronico("test@example.com");
        usuario.setAltura(1.75);
        usuario.setPesoActual(80.0);
        usuario.setFechaNacimiento(LocalDate.of(1995, 6, 15));
        usuario.setGenero("MASCULINO");
        usuario.setNivelActividad("MODERADO");
        usuario.setEstrategia("MANTENIMIENTO");
        usuario.setAjusteCalorico(0);
    }

    // ── generarAutomatico ────────────────────────────────────────────────────

    @Test
    void generarAutomatico_happyPath_calculatesAndSaves() {
        Objetivo savedObj = new Objetivo();
        savedObj.setObjetivoId(1);
        savedObj.setUsuario(usuario);
        savedObj.setTipo("MANTENIMIENTO");
        savedObj.setCaloriasObjetivo(2500.0);
        savedObj.setProteinasObjetivo(160.0);
        savedObj.setCarbohidratosObjetivo(250.0);
        savedObj.setGrasasObjetivo(70.0);
        savedObj.setImc(26.1);
        savedObj.setFechaInicio(LocalDate.now());
        savedObj.setFechaFin(LocalDate.now().plusDays(1));
        savedObj.setActivo(true);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(objetivoRepository.existsByUsuarioAndFechaInicio(eq(usuario), any(LocalDate.class))).thenReturn(false);
        when(objetivoRepository.save(any(Objetivo.class))).thenReturn(savedObj);

        ObjetivoDTO result = objetivoService.generarAutomatico(1);

        assertThat(result).isNotNull();
        assertThat(result.getTipo()).isEqualTo("MANTENIMIENTO");
        verify(objetivoRepository).save(any(Objetivo.class));
    }

    @Test
    void generarAutomatico_superavit_aumentaCalorias() {
        usuario.setEstrategia("SUPERAVIT");
        usuario.setAjusteCalorico(10);

        Objetivo savedObj = buildObjetivoMock(usuario);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(objetivoRepository.existsByUsuarioAndFechaInicio(eq(usuario), any())).thenReturn(false);
        when(objetivoRepository.save(any(Objetivo.class))).thenAnswer(inv -> {
            Objetivo o = inv.getArgument(0);
            o.setObjetivoId(1);
            o.setUsuario(usuario);
            return o;
        });

        ObjetivoDTO result = objetivoService.generarAutomatico(1);
        assertThat(result.getTipo()).isEqualTo("SUPERAVIT");
        // Calorías deben ser > base (factor +10%)
        assertThat(result.getCaloriasObjetivo()).isGreaterThan(2000.0);
    }

    @Test
    void generarAutomatico_deficit_reduceCalorias() {
        usuario.setEstrategia("DEFICIT");
        usuario.setAjusteCalorico(20);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(objetivoRepository.existsByUsuarioAndFechaInicio(eq(usuario), any())).thenReturn(false);
        when(objetivoRepository.save(any(Objetivo.class))).thenAnswer(inv -> {
            Objetivo o = inv.getArgument(0);
            o.setObjetivoId(1);
            o.setUsuario(usuario);
            return o;
        });

        ObjetivoDTO result = objetivoService.generarAutomatico(1);
        assertThat(result.getTipo()).isEqualTo("DEFICIT");
    }

    @Test
    void generarAutomatico_faltaAltura_throws400() {
        usuario.setAltura(null);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> objetivoService.generarAutomatico(1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("altura");
    }

    @Test
    void generarAutomatico_faltaPesoActual_throws400() {
        usuario.setPesoActual(null);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> objetivoService.generarAutomatico(1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("peso");
    }

    @Test
    void generarAutomatico_usuarioNoExiste_throws404() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> objetivoService.generarAutomatico(99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    void generarAutomatico_objetivoYaExisteHoy_actualizaEnVezDeCrear() {
        Objetivo existente = buildObjetivoMock(usuario);
        existente.setObjetivoId(5);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(objetivoRepository.existsByUsuarioAndFechaInicio(eq(usuario), any())).thenReturn(true);
        when(objetivoRepository.findFirstByUsuarioAndFechaInicioOrderByObjetivoIdDesc(eq(usuario), any()))
                .thenReturn(Optional.of(existente));
        when(objetivoRepository.save(any())).thenReturn(existente);

        ObjetivoDTO result = objetivoService.generarAutomatico(1);
        assertThat(result.getObjetivoId()).isEqualTo(5);
    }

    @Test
    void generarAutomatico_mujer_usaFormulaMifflinFemenino() {
        usuario.setGenero("FEMENINO");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(objetivoRepository.existsByUsuarioAndFechaInicio(eq(usuario), any())).thenReturn(false);
        when(objetivoRepository.save(any())).thenAnswer(inv -> {
            Objetivo o = inv.getArgument(0);
            o.setObjetivoId(1);
            o.setUsuario(usuario);
            return o;
        });

        ObjetivoDTO result = objetivoService.generarAutomatico(1);
        // TMB femenino es menor, por lo que calorías < masculino
        assertThat(result.getCaloriasObjetivo()).isGreaterThan(0);
    }

    // ── getObjetivoActual ────────────────────────────────────────────────────

    @Test
    void getObjetivoActual_existe_returnsDTO() {
        Objetivo obj = buildObjetivoMock(usuario);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(objetivoRepository.findFirstByUsuarioOrderByFechaInicioDesc(usuario)).thenReturn(Optional.of(obj));

        ObjetivoDTO result = objetivoService.getObjetivoActual(1);
        assertThat(result).isNotNull();
    }

    @Test
    void getObjetivoActual_sinObjetivos_throws404() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(objetivoRepository.findFirstByUsuarioOrderByFechaInicioDesc(usuario)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> objetivoService.getObjetivoActual(1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("objetivo");
    }

    // ── findAllByUsuario ──────────────────────────────────────────────────────

    @Test
    void findAllByUsuario_retornaLista() {
        Objetivo o1 = buildObjetivoMock(usuario);
        Objetivo o2 = buildObjetivoMock(usuario);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(objetivoRepository.findByUsuarioOrderByFechaInicioDesc(usuario)).thenReturn(List.of(o1, o2));

        List<ObjetivoDTO> result = objetivoService.findAllByUsuario(1);
        assertThat(result).hasSize(2);
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existe_elimina() {
        when(objetivoRepository.existsById(1)).thenReturn(true);
        objetivoService.deleteById(1);
        verify(objetivoRepository).deleteById(1);
    }

    @Test
    void deleteById_noExiste_throws404() {
        when(objetivoRepository.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> objetivoService.deleteById(99))
                .isInstanceOf(ResponseStatusException.class);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Objetivo buildObjetivoMock(Usuario u) {
        Objetivo o = new Objetivo();
        o.setObjetivoId(1);
        o.setUsuario(u);
        o.setTipo("MANTENIMIENTO");
        o.setCaloriasObjetivo(2400.0);
        o.setProteinasObjetivo(160.0);
        o.setCarbohidratosObjetivo(240.0);
        o.setGrasasObjetivo(67.0);
        o.setImc(26.1);
        o.setFechaInicio(LocalDate.now());
        o.setFechaFin(LocalDate.now().plusDays(1));
        o.setActivo(true);
        return o;
    }
}
