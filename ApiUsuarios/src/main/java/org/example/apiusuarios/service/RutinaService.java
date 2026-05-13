package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.RutinaDTO;
import org.example.apiusuarios.model.Rutina;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RutinaRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RutinaService {

    @Autowired
    private RutinaRepository rutinaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public RutinaDTO save(RutinaDTO dto) {
        if (dto.getUsuarioId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la rutina es obligatorio");
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        Rutina r = new Rutina();
        r.setNombre(dto.getNombre());
        r.setEtiqueta(dto.getEtiqueta());
        r.setDiasActivos(dto.getDiasActivos());
        r.setDuracionEstimadaMin(dto.getDuracionEstimadaMin());
        r.setUsuario(usuario);
        return new RutinaDTO(rutinaRepository.save(r));
    }

    public List<RutinaDTO> findByUsuario(Integer usuarioId) {
        return rutinaRepository.findByUsuario_UsuarioId(usuarioId)
                .stream().map(RutinaDTO::new).collect(Collectors.toList());
    }

    public List<RutinaDTO> findAll() {
        return rutinaRepository.findAll().stream().map(RutinaDTO::new).collect(Collectors.toList());
    }

    public RutinaDTO findById(Integer id) {
        return new RutinaDTO(rutinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada")));
    }

    public RutinaDTO update(Integer id, RutinaDTO dto) {
        Rutina r = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));
        if (dto.getNombre() != null) r.setNombre(dto.getNombre());
        if (dto.getEtiqueta() != null) r.setEtiqueta(dto.getEtiqueta());
        if (dto.getDiasActivos() != null) r.setDiasActivos(dto.getDiasActivos());
        if (dto.getDuracionEstimadaMin() != null) r.setDuracionEstimadaMin(dto.getDuracionEstimadaMin());
        return new RutinaDTO(rutinaRepository.save(r));
    }

    @Autowired
    private org.example.apiusuarios.repository.SesionEntrenamientoRepository sesionRepository;
    
    @Autowired
    private org.example.apiusuarios.repository.SerieRealizadaRepository serieRealizadaRepository;

    @Transactional
    public void deleteById(Integer id) {
        Rutina rutina = rutinaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));
        
        // Desvincular las sesiones de entrenamiento para no perder el historial al borrar la rutina
        List<org.example.apiusuarios.model.SesionEntrenamiento> sesiones = sesionRepository.findByRutina_RutinaId(id);
        for (org.example.apiusuarios.model.SesionEntrenamiento sesion : sesiones) {
            sesion.setRutina(null);
            sesionRepository.save(sesion);
        }
        
        // Desvincular también los ejercicios de las series realizadas en el historial
        if (rutina.getEjercicios() != null) {
            for (org.example.apiusuarios.model.Ejercicio ej : rutina.getEjercicios()) {
                List<org.example.apiusuarios.model.SerieRealizada> series = serieRealizadaRepository.findByEjercicio_EjercicioId(ej.getEjercicioId());
                for (org.example.apiusuarios.model.SerieRealizada serie : series) {
                    serie.setEjercicio(null);
                    serieRealizadaRepository.save(serie);
                }
            }
        }
            
        rutinaRepository.deleteById(id);
    }
}
