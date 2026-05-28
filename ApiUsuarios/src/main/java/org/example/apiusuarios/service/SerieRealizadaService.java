package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.SerieRealizadaDTO;
import org.example.apiusuarios.model.Ejercicio;
import org.example.apiusuarios.model.SerieRealizada;
import org.example.apiusuarios.model.SesionEntrenamiento;
import org.example.apiusuarios.repository.EjercicioRepository;
import org.example.apiusuarios.repository.SerieRealizadaRepository;
import org.example.apiusuarios.repository.SesionEntrenamientoRepository;
import org.example.apiusuarios.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SerieRealizadaService {

    @Autowired private SerieRealizadaRepository repo;
    @Autowired private SesionEntrenamientoRepository sesionRepo;
    @Autowired private EjercicioRepository ejercicioRepository;
    @Autowired private RecordPersonalService recordPersonalService;
    @Autowired private SecurityUtils securityUtils;

    public SerieRealizadaDTO save(SerieRealizadaDTO dto) {
        if (dto.getSesionId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sesionId es obligatorio");
        SesionEntrenamiento sesion = sesionRepo.findById(dto.getSesionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
        securityUtils.assertEsDuenoOAdmin(sesion.getUsuario().getUsuarioId());
        SerieRealizada s = new SerieRealizada();
        s.setSesion(sesion);
        s.setPesoKg(dto.getPesoKg());
        s.setRepeticionesRealizadas(dto.getRepeticionesRealizadas());
        s.setCompletado(dto.getCompletado() != null ? dto.getCompletado() : false);
        s.setOrden(dto.getOrden());
        if (dto.getEjercicioId() != null) {
            Ejercicio ejercicio = ejercicioRepository.findById(dto.getEjercicioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado"));
            s.setEjercicio(ejercicio);
            if (Boolean.TRUE.equals(dto.getCompletado()) && ejercicio.getEjercicioGlobal() != null) {
                recordPersonalService.actualizarSiMejora(
                        sesion.getUsuario(),
                        ejercicio.getEjercicioGlobal(),
                        dto.getPesoKg(),
                        dto.getRepeticionesRealizadas());
            }
        }
        return new SerieRealizadaDTO(repo.save(s));
    }

    public List<SerieRealizadaDTO> findBySesion(Integer sesionId) {
        SesionEntrenamiento sesion = sesionRepo.findById(sesionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
        securityUtils.assertEsDuenoOAdmin(sesion.getUsuario().getUsuarioId());
        return repo.findBySesion_SesionIdOrderByOrdenAsc(sesionId)
                .stream().map(SerieRealizadaDTO::new).collect(Collectors.toList());
    }

    public SerieRealizadaDTO update(Integer id, SerieRealizadaDTO dto) {
        SerieRealizada s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serie no encontrada"));
        securityUtils.assertEsDuenoOAdmin(s.getSesion().getUsuario().getUsuarioId());
        if (dto.getPesoKg() != null) s.setPesoKg(dto.getPesoKg());
        if (dto.getRepeticionesRealizadas() != null) s.setRepeticionesRealizadas(dto.getRepeticionesRealizadas());
        if (dto.getCompletado() != null) s.setCompletado(dto.getCompletado());
        if (dto.getOrden() != null) s.setOrden(dto.getOrden());
        return new SerieRealizadaDTO(repo.save(s));
    }

    public void deleteById(Integer id) {
        SerieRealizada s = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serie no encontrada"));
        securityUtils.assertEsDuenoOAdmin(s.getSesion().getUsuario().getUsuarioId());
        repo.delete(s);
    }
}
