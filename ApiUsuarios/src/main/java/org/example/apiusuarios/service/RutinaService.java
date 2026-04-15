package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.RutinaDTO;
import org.example.apiusuarios.model.Rutina;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RutinaRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    public void deleteById(Integer id) {
        if (!rutinaRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada");
        rutinaRepository.deleteById(id);
    }
}
