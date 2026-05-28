package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.SesionEntrenamientoDTO;
import org.example.apiusuarios.model.Rutina;
import org.example.apiusuarios.model.SesionEntrenamiento;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RutinaRepository;
import org.example.apiusuarios.repository.SesionEntrenamientoRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.example.apiusuarios.security.SecurityUtils;
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
    @Autowired private SecurityUtils securityUtils;

    public SesionEntrenamientoDTO iniciar(SesionEntrenamientoDTO dto) {
        if (dto.getUsuarioId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");
        securityUtils.assertEsDuenoOAdmin(dto.getUsuarioId());
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
        if (dto.getNotasUsuario() != null) s.setNotasUsuario(dto.getNotasUsuario());
        return new SesionEntrenamientoDTO(repo.save(s));
    }

    public SesionEntrenamientoDTO finalizar(Integer sesionId, SesionEntrenamientoDTO dto) {
        SesionEntrenamiento s = repo.findById(sesionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
        securityUtils.assertEsDuenoOAdmin(s.getUsuario().getUsuarioId());
        LocalDateTime fin = LocalDateTime.now();
        s.setFechaFin(fin);
        s.setDuracionMinutos((int) ChronoUnit.MINUTES.between(s.getFechaInicio(), fin));
        if (dto.getNivelIntensidad() != null) s.setNivelIntensidad(dto.getNivelIntensidad());
        if (dto.getNivelRecuperacion() != null) s.setNivelRecuperacion(dto.getNivelRecuperacion());
        if (dto.getNotasUsuario() != null) s.setNotasUsuario(dto.getNotasUsuario());
        return new SesionEntrenamientoDTO(repo.save(s));
    }

    public List<SesionEntrenamientoDTO> findByUsuario(Integer usuarioId) {
        securityUtils.assertEsDuenoOAdmin(usuarioId);
        return repo.findByUsuario_UsuarioIdOrderByFechaInicioDesc(usuarioId)
                .stream().map(SesionEntrenamientoDTO::new).collect(Collectors.toList());
    }

    public SesionEntrenamientoDTO findById(Integer id) {
        SesionEntrenamiento s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
        securityUtils.assertEsDuenoOAdmin(s.getUsuario().getUsuarioId());
        return new SesionEntrenamientoDTO(s);
    }

    public void deleteById(Integer id) {
        SesionEntrenamiento s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
        securityUtils.assertEsDuenoOAdmin(s.getUsuario().getUsuarioId());
        repo.delete(s);
    }
}
