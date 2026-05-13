package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.FotoProgresoDTO;
import org.example.apiusuarios.model.FotoProgreso;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.FotoProgresoRepository;
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
class FotoProgresoServiceTest {

    @Mock FotoProgresoRepository repo;
    @Mock UsuarioRepository usuarioRepository;

    @InjectMocks FotoProgresoService service;

    private Usuario usuario;
    private FotoProgreso foto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsuarioId(1);

        foto = new FotoProgreso();
        foto.setFotoId(1);
        foto.setUsuario(usuario);
        foto.setUrlFoto("https://cdn.fitwin.app/fotos/foto1.jpg");
        foto.setFecha(LocalDate.now());
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    void save_happyPath_retornaDTO() {
        FotoProgresoDTO dto = new FotoProgresoDTO();
        dto.setUsuarioId(1);
        dto.setUrlFoto("https://cdn.fitwin.app/fotos/foto1.jpg");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(repo.save(any())).thenReturn(foto);

        FotoProgresoDTO result = service.save(dto);
        assertThat(result.getUrlFoto()).isEqualTo("https://cdn.fitwin.app/fotos/foto1.jpg");
        verify(repo).save(any(FotoProgreso.class));
    }

    @Test
    void save_sinUsuarioId_throws400() {
        FotoProgresoDTO dto = new FotoProgresoDTO();
        dto.setUrlFoto("https://cdn.fitwin.app/fotos/foto1.jpg");
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("usuarioId");
    }

    @Test
    void save_sinUrlFoto_throws400() {
        FotoProgresoDTO dto = new FotoProgresoDTO();
        dto.setUsuarioId(1);
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("urlFoto");
    }

    @Test
    void save_urlFotoVacia_throws400() {
        FotoProgresoDTO dto = new FotoProgresoDTO();
        dto.setUsuarioId(1);
        dto.setUrlFoto("   ");
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("urlFoto");
    }

    @Test
    void save_usuarioNoExiste_throws404() {
        FotoProgresoDTO dto = new FotoProgresoDTO();
        dto.setUsuarioId(99);
        dto.setUrlFoto("https://cdn.fitwin.app/fotos/foto1.jpg");
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.save(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Usuario");
    }

    // ── findByUsuario ─────────────────────────────────────────────────────────

    @Test
    void findByUsuario_retornaOrdenadoPorFechaDesc() {
        when(repo.findByUsuario_UsuarioIdOrderByFechaDesc(1)).thenReturn(List.of(foto));
        assertThat(service.findByUsuario(1)).hasSize(1);
    }

    @Test
    void findByUsuario_sinFotos_retornaVacio() {
        when(repo.findByUsuario_UsuarioIdOrderByFechaDesc(1)).thenReturn(List.of());
        assertThat(service.findByUsuario(1)).isEmpty();
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
