package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.EjercicioGlobalDTO;
import org.example.apiusuarios.model.CategoriaEjercicio;
import org.example.apiusuarios.model.EjercicioGlobal;
import org.example.apiusuarios.repository.EjercicioGlobalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EjercicioGlobalService {

    @Autowired
    private EjercicioGlobalRepository repo;

    public EjercicioGlobalDTO save(EjercicioGlobalDTO dto) {
        EjercicioGlobal e = new EjercicioGlobal();
        e.setNombre(dto.getNombre());
        e.setCategoria(dto.getCategoria());
        e.setMusculoPrimario(dto.getMusculoPrimario());
        e.setMusculosSecundarios(dto.getMusculosSecundarios());
        e.setEquipamiento(dto.getEquipamiento());
        e.setCueCoaching(dto.getCueCoaching());
        return new EjercicioGlobalDTO(repo.save(e));
    }

    public List<EjercicioGlobalDTO> findAll() {
        return repo.findAll().stream().map(EjercicioGlobalDTO::new).collect(Collectors.toList());
    }

    public List<EjercicioGlobalDTO> findByCategoria(CategoriaEjercicio categoria) {
        return repo.findByCategoria(categoria).stream().map(EjercicioGlobalDTO::new).collect(Collectors.toList());
    }

    public List<EjercicioGlobalDTO> search(String nombre) {
        return repo.findByNombreContainingIgnoreCase(nombre).stream().map(EjercicioGlobalDTO::new).collect(Collectors.toList());
    }

    public EjercicioGlobalDTO findById(Integer id) {
        return new EjercicioGlobalDTO(repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio global no encontrado")));
    }

    public EjercicioGlobalDTO update(Integer id, EjercicioGlobalDTO dto) {
        EjercicioGlobal e = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio global no encontrado"));
        if (dto.getNombre() != null) e.setNombre(dto.getNombre());
        if (dto.getCategoria() != null) e.setCategoria(dto.getCategoria());
        if (dto.getMusculoPrimario() != null) e.setMusculoPrimario(dto.getMusculoPrimario());
        if (dto.getMusculosSecundarios() != null) e.setMusculosSecundarios(dto.getMusculosSecundarios());
        if (dto.getEquipamiento() != null) e.setEquipamiento(dto.getEquipamiento());
        if (dto.getCueCoaching() != null) e.setCueCoaching(dto.getCueCoaching());
        return new EjercicioGlobalDTO(repo.save(e));
    }

    public void deleteById(Integer id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio global no encontrado");
        repo.deleteById(id);
    }
}
