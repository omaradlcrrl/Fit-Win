package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.SesionEntrenamientoDTO;
import org.example.apiusuarios.model.Rutina;
import org.example.apiusuarios.model.SesionEntrenamiento;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RutinaRepository;
import org.example.apiusuarios.repository.SesionEntrenamientoRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SesionEntrenamientoService {

    @Autowired private SesionEntrenamientoRepository repo;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RutinaRepository rutinaRepository;

    public SesionEntrenamientoDTO iniciar(SesionEntrenamientoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        SesionEntrenamiento s = new SesionEntrenamiento();
        s.setUsuario(usuario);
        s.setFechaInicio(LocalDateTime.now());
        if (dto.getRutinaId() != null) {
            Rutina rutina = rutinaRepository.findById(dto.getRutinaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));
            s.setRutina(rutina);
        }
        if (dto.getNivelIntensidad() != null) s.setNivelIntensidad(dto.getNivelIntensidad());
        if (dto.getNivelRecuperacion() != null) s.setNivelRecuperacion(dto.getNivelRecuperacion());
        return new SesionEntrenamientoDTO(repo.save(s));
    }

    public SesionEntrenamientoDTO finalizar(Integer sesionId, SesionEntrenamientoDTO dto) {
        SesionEntrenamiento s = repo.findById(sesionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
        LocalDateTime fin = LocalDateTime.now();
        s.setFechaFin(fin);
        s.setDuracionMinutos((int) ChronoUnit.MINUTES.between(s.getFechaInicio(), fin));
        if (dto.getNivelIntensidad() != null) s.setNivelIntensidad(dto.getNivelIntensidad());
        if (dto.getNivelRecuperacion() != null) s.setNivelRecuperacion(dto.getNivelRecuperacion());
        return new SesionEntrenamientoDTO(repo.save(s));
    }

    public List<SesionEntrenamientoDTO> findByUsuario(Integer usuarioId) {
        return repo.findByUsuario_UsuarioIdOrderByFechaInicioDesc(usuarioId)
                .stream().map(SesionEntrenamientoDTO::new).collect(Collectors.toList());
    }

    public SesionEntrenamientoDTO findById(Integer id) {
        return new SesionEntrenamientoDTO(repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada")));
    }

    public void deleteById(Integer id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada");
        repo.deleteById(id);
    }
}
