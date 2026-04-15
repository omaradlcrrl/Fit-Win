package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.RecordPersonalDTO;
import org.example.apiusuarios.model.EjercicioGlobal;
import org.example.apiusuarios.model.RecordPersonal;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.EjercicioGlobalRepository;
import org.example.apiusuarios.repository.RecordPersonalRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordPersonalService {

    @Autowired private RecordPersonalRepository repo;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EjercicioGlobalRepository ejercicioGlobalRepository;

    public RecordPersonalDTO save(RecordPersonalDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        EjercicioGlobal eg = ejercicioGlobalRepository.findById(dto.getEjercicioGlobalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio global no encontrado"));
        RecordPersonal r = new RecordPersonal();
        r.setUsuario(usuario);
        r.setEjercicioGlobal(eg);
        r.setPesoKg(dto.getPesoKg());
        r.setFecha(dto.getFecha());
        return new RecordPersonalDTO(repo.save(r));
    }

    public List<RecordPersonalDTO> findByUsuario(Integer usuarioId) {
        return repo.findByUsuario_UsuarioIdOrderByFechaDesc(usuarioId)
                .stream().map(RecordPersonalDTO::new).collect(Collectors.toList());
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
