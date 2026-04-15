package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.FotoProgresoDTO;
import org.example.apiusuarios.model.FotoProgreso;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.FotoProgresoRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FotoProgresoService {

    @Autowired private FotoProgresoRepository repo;
    @Autowired private UsuarioRepository usuarioRepository;

    public FotoProgresoDTO save(FotoProgresoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        FotoProgreso f = new FotoProgreso();
        f.setUsuario(usuario);
        f.setUrlFoto(dto.getUrlFoto());
        f.setFecha(dto.getFecha());
        return new FotoProgresoDTO(repo.save(f));
    }

    public List<FotoProgresoDTO> findByUsuario(Integer usuarioId) {
        return repo.findByUsuario_UsuarioIdOrderByFechaDesc(usuarioId)
                .stream().map(FotoProgresoDTO::new).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto no encontrada");
        repo.deleteById(id);
    }
}
