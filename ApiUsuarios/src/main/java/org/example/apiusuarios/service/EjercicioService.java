package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.EjercicioDTO;
import org.example.apiusuarios.model.Ejercicio;
import org.example.apiusuarios.model.EjercicioGlobal;
import org.example.apiusuarios.model.Rutina;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.EjercicioGlobalRepository;
import org.example.apiusuarios.repository.EjercicioRepository;
import org.example.apiusuarios.repository.RutinaRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EjercicioService {

    @Autowired private EjercicioRepository ejercicioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RutinaRepository rutinaRepository;
    @Autowired private EjercicioGlobalRepository ejercicioGlobalRepository;

    public EjercicioDTO save(EjercicioDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Ejercicio e = new Ejercicio();
        e.setUsuario(usuario);
        e.setDiaSemana(dto.getDiaSemana());
        e.setSeries(dto.getSeries());
        e.setRepeticionesMin(dto.getRepeticionesMin());
        e.setRepeticionesMax(dto.getRepeticionesMax());
        e.setDescansoSegundos(dto.getDescansoSegundos());
        e.setPesoKg(dto.getPesoKg());
        e.setPosicion(dto.getPosicion());

        if (dto.getRutinaId() != null) {
            Rutina rutina = rutinaRepository.findById(dto.getRutinaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));
            e.setRutina(rutina);
        }
        if (dto.getEjercicioGlobalId() != null) {
            EjercicioGlobal global = ejercicioGlobalRepository.findById(dto.getEjercicioGlobalId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio global no encontrado"));
            e.setEjercicioGlobal(global);
        }
        return new EjercicioDTO(ejercicioRepository.save(e));
    }

    public List<EjercicioDTO> findByUsuarioAndDia(Integer usuarioId, String diaSemana) {
        return ejercicioRepository.findByUsuario_UsuarioIdAndDiaSemanaOrderByPosicionAsc(usuarioId, diaSemana)
                .stream().map(EjercicioDTO::new).collect(Collectors.toList());
    }

    public List<EjercicioDTO> findByRutina(Integer rutinaId) {
        return ejercicioRepository.findByRutina_RutinaId(rutinaId)
                .stream().map(EjercicioDTO::new).collect(Collectors.toList());
    }

    public List<EjercicioDTO> findAll() {
        return ejercicioRepository.findAll().stream().map(EjercicioDTO::new).collect(Collectors.toList());
    }

    public EjercicioDTO findById(Integer id) {
        return new EjercicioDTO(ejercicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado")));
    }

    public EjercicioDTO update(Integer id, EjercicioDTO dto) {
        Ejercicio e = ejercicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado"));
        if (dto.getDiaSemana() != null) e.setDiaSemana(dto.getDiaSemana());
        if (dto.getSeries() != null) e.setSeries(dto.getSeries());
        if (dto.getRepeticionesMin() != null) e.setRepeticionesMin(dto.getRepeticionesMin());
        if (dto.getRepeticionesMax() != null) e.setRepeticionesMax(dto.getRepeticionesMax());
        if (dto.getDescansoSegundos() != null) e.setDescansoSegundos(dto.getDescansoSegundos());
        if (dto.getPesoKg() != null) e.setPesoKg(dto.getPesoKg());
        if (dto.getPosicion() != null) e.setPosicion(dto.getPosicion());
        return new EjercicioDTO(ejercicioRepository.save(e));
    }

    public void deleteById(Integer id) {
        if (!ejercicioRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado");
        ejercicioRepository.deleteById(id);
    }
}
