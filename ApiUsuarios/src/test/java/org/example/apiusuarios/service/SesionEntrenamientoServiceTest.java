package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.SesionEntrenamientoDTO;
import org.example.apiusuarios.model.Rutina;
import org.example.apiusuarios.model.SesionEntrenamiento;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RutinaRepository;
import org.example.apiusuarios.repository.SesionEntrenamientoRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SesionEntrenamientoServiceTest {

    @Mock SesionEntrenamientoRepository repo;
    @Mock UsuarioRepository usuarioRepository;
    @Mock RutinaRepository rutinaRepository;

    @InjectMocks SesionEntrenamientoService sesionService;

    private Usuario usuario;
    private Rutina rutina;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(1);
        usuario.setNombre("Test");

        rutina = new Rutina();
        rutina.setRutinaId(10);
        rutina.setNombre("Rutina A");
    }

    // ── iniciar ───────────────────────────────────────────────────────────────

    @Test
    void iniciar_sinRutina_creaYGuardaSesion() {
        SesionEntrenamiento saved = new SesionEntrenamiento();
        saved.setSesionId(1);
        saved.setUsuario(usuario);
        saved.setFechaInicio(LocalDateTime.now());

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(repo.save(any())).thenReturn(saved);

        SesionEntrenamientoDTO dto = new SesionEntrenamientoDTO();
        dto.setUsuarioId(1);

        SesionEntrenamientoDTO result = sesionService.iniciar(dto);

        assertThat(result.getSesionId()).isEqualTo(1);
        verify(repo).save(any(SesionEntrenamiento.class));
    }

    @Test
    void iniciar_conRutina_asignaRutinaASesion() {
        SesionEntrenamiento saved = new SesionEntrenamiento();
        saved.setSesionId(2);
        saved.setUsuario(usuario);
        saved.setRutina(rutina);
        saved.setFechaInicio(LocalDateTime.now());

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(rutinaRepository.findById(10)).thenReturn(Optional.of(rutina));
        when(repo.save(any())).thenReturn(saved);

        SesionEntrenamientoDTO dto = new SesionEntrenamientoDTO();
        dto.setUsuarioId(1);
        dto.setRutinaId(10);

        SesionEntrenamientoDTO result = sesionService.iniciar(dto);

        assertThat(result.getRutinaId()).isEqualTo(10);
    }

    @Test
    void iniciar_rutinaNoExiste_throws404() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(rutinaRepository.findById(99)).thenReturn(Optional.empty());

        SesionEntrenamientoDTO dto = new SesionEntrenamientoDTO();
        dto.setUsuarioId(1);
        dto.setRutinaId(99);

        assertThatThrownBy(() -> sesionService.iniciar(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Rutina");
    }

    @Test
    void iniciar_usuarioNoExiste_throws404() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        SesionEntrenamientoDTO dto = new SesionEntrenamientoDTO();
        dto.setUsuarioId(99);

        assertThatThrownBy(() -> sesionService.iniciar(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario");
    }

    @Test
    void iniciar_asignaFechaInicioAutomaticamente() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(repo.save(any())).thenAnswer(inv -> {
            SesionEntrenamiento s = inv.getArgument(0);
            s.setSesionId(1);
            return s;
        });

        SesionEntrenamientoDTO dto = new SesionEntrenamientoDTO();
        dto.setUsuarioId(1);

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        sesionService.iniciar(dto);

        ArgumentCaptor<SesionEntrenamiento> captor = ArgumentCaptor.forClass(SesionEntrenamiento.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getFechaInicio()).isAfter(antes);
    }

    // ── finalizar ─────────────────────────────────────────────────────────────

    @Test
    void finalizar_calculaDuracionMinutos() {
        SesionEntrenamiento sesion = new SesionEntrenamiento();
        sesion.setSesionId(1);
        sesion.setUsuario(usuario);
        sesion.setFechaInicio(LocalDateTime.now().minusMinutes(45));

        when(repo.findById(1)).thenReturn(Optional.of(sesion));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SesionEntrenamientoDTO dto = new SesionEntrenamientoDTO();
        SesionEntrenamientoDTO result = sesionService.finalizar(1, dto);

        assertThat(result.getDuracionMinutos()).isGreaterThanOrEqualTo(44);
        assertThat(result.getDuracionMinutos()).isLessThanOrEqualTo(46);
    }

    @Test
    void finalizar_asignaFechaFin() {
        SesionEntrenamiento sesion = new SesionEntrenamiento();
        sesion.setSesionId(1);
        sesion.setFechaInicio(LocalDateTime.now().minusMinutes(10));

        when(repo.findById(1)).thenReturn(Optional.of(sesion));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SesionEntrenamientoDTO result = sesionService.finalizar(1, new SesionEntrenamientoDTO());

        assertThat(result.getFechaFin()).isNotNull();
    }

    @Test
    void finalizar_sesionNoExiste_throws404() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sesionService.finalizar(99, new SesionEntrenamientoDTO()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Sesión");
    }

    @Test
    void finalizar_actualizaNivelIntensidad() {
        SesionEntrenamiento sesion = new SesionEntrenamiento();
        sesion.setSesionId(1);
        sesion.setFechaInicio(LocalDateTime.now().minusMinutes(30));

        when(repo.findById(1)).thenReturn(Optional.of(sesion));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SesionEntrenamientoDTO dto = new SesionEntrenamientoDTO();
        dto.setNivelIntensidad(8);
        dto.setNivelRecuperacion(7);

        SesionEntrenamientoDTO result = sesionService.finalizar(1, dto);

        assertThat(result.getNivelIntensidad()).isEqualTo(8);
        assertThat(result.getNivelRecuperacion()).isEqualTo(7);
    }

    // ── findByUsuario ─────────────────────────────────────────────────────────

    @Test
    void findByUsuario_retornaListaOrdenada() {
        SesionEntrenamiento s1 = new SesionEntrenamiento();
        s1.setSesionId(1);
        s1.setUsuario(usuario);
        s1.setFechaInicio(LocalDateTime.now().minusDays(1));

        SesionEntrenamiento s2 = new SesionEntrenamiento();
        s2.setSesionId(2);
        s2.setUsuario(usuario);
        s2.setFechaInicio(LocalDateTime.now());

        when(repo.findByUsuario_UsuarioIdOrderByFechaInicioDesc(1)).thenReturn(List.of(s2, s1));

        List<SesionEntrenamientoDTO> result = sesionService.findByUsuario(1);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSesionId()).isEqualTo(2);
    }

    // ── deleteById ────────────────────────────────────────────────────────────

    @Test
    void deleteById_existe_elimina() {
        when(repo.existsById(1)).thenReturn(true);
        sesionService.deleteById(1);
        verify(repo).deleteById(1);
    }

    @Test
    void deleteById_noExiste_throws404() {
        when(repo.existsById(99)).thenReturn(false);
        assertThatThrownBy(() -> sesionService.deleteById(99))
                .isInstanceOf(ResponseStatusException.class);
    }
}
