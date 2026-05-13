package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.RecordPersonalDTO;
import org.example.apiusuarios.model.EjercicioGlobal;
import org.example.apiusuarios.model.RecordPersonal;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.EjercicioGlobalRepository;
import org.example.apiusuarios.repository.RecordPersonalRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.example.apiusuarios.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordPersonalService {

    @Autowired
    private RecordPersonalRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EjercicioGlobalRepository ejercicioGlobalRepository;

    public RecordPersonalDTO save(RecordPersonalDTO dto) {
        if (dto.getUsuarioId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");
        if (dto.getEjercicioGlobalId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ejercicioGlobalId es obligatorio");
        if (dto.getPesoKg() == null && dto.getRepeticiones() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pesoKg o repeticiones son obligatorios");
        if (dto.getFecha() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fecha es obligatoria");
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        EjercicioGlobal eg = ejercicioGlobalRepository.findById(dto.getEjercicioGlobalId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ejercicio global no encontrado"));
        RecordPersonal r = new RecordPersonal();
        r.setUsuario(usuario);
        r.setEjercicioGlobal(eg);
        r.setPesoKg(dto.getPesoKg());
        r.setRepeticiones(dto.getRepeticiones());
        r.setFecha(dto.getFecha());
        return new RecordPersonalDTO(repo.save(r));
    }

    public List<RecordPersonalDTO> findByUsuario(Integer usuarioId) {
        return repo.findByUsuario_UsuarioIdOrderByFechaDesc(usuarioId)
                .stream().map(RecordPersonalDTO::new).collect(Collectors.toList());
    }

    public List<RecordPersonalDTO> findByUsuarioAndEjercicio(Integer usuarioId, Integer ejercicioGlobalId) {
        return repo.findByUsuario_UsuarioIdAndEjercicioGlobal_EjercicioGlobalId(usuarioId, ejercicioGlobalId)
                .stream().map(RecordPersonalDTO::new).collect(Collectors.toList());
    }

    public RecordPersonalDTO findMaxByUsuarioAndEjercicio(Integer usuarioId, Integer ejercicioGlobalId) {
        return repo.findByUsuario_UsuarioIdAndEjercicioGlobal_EjercicioGlobalId(usuarioId, ejercicioGlobalId)
                .stream()
                .max(java.util.Comparator.comparingDouble(r -> r.getPesoKg() != null ? r.getPesoKg() : 0))
                .map(RecordPersonalDTO::new)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay record para ese ejercicio"));
    }

    public RecordPersonalDTO actualizarSiMejora(Usuario usuario, EjercicioGlobal ejercicioGlobal, Double pesoKg, Integer repeticiones) {
        if (pesoKg == null && repeticiones == null) return null;
        List<RecordPersonal> existentes = repo.findByUsuario_UsuarioIdAndEjercicioGlobal_EjercicioGlobalId(
                usuario.getUsuarioId(), ejercicioGlobal.getEjercicioGlobalId());
        boolean esMejora = existentes.stream().noneMatch(r ->
                (pesoKg != null && r.getPesoKg() != null && r.getPesoKg() >= pesoKg) ||
                (repeticiones != null && r.getRepeticiones() != null && r.getRepeticiones() >= repeticiones));
        if (existentes.isEmpty() || esMejora) {
            RecordPersonal nuevo = new RecordPersonal();
            nuevo.setUsuario(usuario);
            nuevo.setEjercicioGlobal(ejercicioGlobal);
            nuevo.setPesoKg(pesoKg);
            nuevo.setRepeticiones(repeticiones);
            return new RecordPersonalDTO(repo.save(nuevo));
        }
        return null;
    }

    public RecordPersonalDTO findById(Integer id) {
        return new RecordPersonalDTO(repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record no encontrado")));
    }

    public void deleteById(Integer id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record no encontrado");
        repo.deleteById(id);
    }
}
