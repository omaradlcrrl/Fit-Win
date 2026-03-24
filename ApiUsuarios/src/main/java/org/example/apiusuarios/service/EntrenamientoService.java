package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.EntrenamientoDTO;
import org.example.apiusuarios.model.Entrenamiento;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.EntrenamientoRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class EntrenamientoService {

    @Autowired
    private EntrenamientoRepository entrenamientoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public EntrenamientoDTO save(EntrenamientoDTO dto) {
        if (dto.getUsuarioId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Entrenamiento e = convertirAEntidad(dto);
        e.setUsuario(usuario);

        if (e.getPosicionEjercicio() == null) {
            int next = entrenamientoRepository.countByUsuarioAndDiaSemana(usuario, e.getDiaSemana()) + 1;
            e.setPosicionEjercicio(next);
        }

        Entrenamiento guardado = entrenamientoRepository.save(e);
        return new EntrenamientoDTO(guardado);
    }

    public void deleteByPosicionYDia(Integer usuarioId, String diaSemana, Integer posicion) {
        if (usuarioId == null || posicion == null || diaSemana == null || diaSemana.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "usuarioId, diaSemana y posicion son obligatorios");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Entrenamiento target = entrenamientoRepository
                .findByUsuarioAndDiaSemanaAndPosicionEjercicio(usuario, diaSemana, posicion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe ejercicio en ese día/posición"));

        entrenamientoRepository.delete(target);

        List<Entrenamiento> delDia = entrenamientoRepository
                .findByUsuarioAndDiaSemanaOrderByPosicionEjercicio(usuario, diaSemana);

        int i = 1;
        for (Entrenamiento e : delDia)
            e.setPosicionEjercicio(i++);
        entrenamientoRepository.saveAll(delDia);
    }

    public EntrenamientoDTO reemplazar(EntrenamientoDTO dto) {
        if (dto.getUsuarioId() == null || dto.getPosicionEjercicio() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId y posicionEjercicio son obligatorios");
        if (dto.getDiaSemana() == null || dto.getDiaSemana().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "diaSemana es obligatorio para reemplazar");

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        java.util.Optional<Entrenamiento> actual = entrenamientoRepository
                .findByUsuarioAndDiaSemanaAndPosicionEjercicio(usuario, dto.getDiaSemana(), dto.getPosicionEjercicio());
        actual.ifPresent(entrenamientoRepository::delete);

        List<Entrenamiento> delDia = entrenamientoRepository
                .findByUsuarioAndDiaSemanaOrderByPosicionEjercicio(usuario, dto.getDiaSemana());

        for (Entrenamiento e : delDia) {
            if (e.getPosicionEjercicio() >= dto.getPosicionEjercicio()) {
                e.setPosicionEjercicio(e.getPosicionEjercicio() + 1);
            }
        }
        entrenamientoRepository.saveAll(delDia);

        Entrenamiento nuevo = convertirAEntidad(dto);
        nuevo.setUsuario(usuario);

        Entrenamiento guardado = entrenamientoRepository.save(nuevo);
        return new EntrenamientoDTO(guardado);
    }

    public List<EntrenamientoDTO> findByUsuarioAndDiaSemanaOrdered(Integer usuarioId, String diaSemana) {
        if (usuarioId == null || diaSemana == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId y diaSemana son obligatorios");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return entrenamientoRepository.findByUsuarioAndDiaSemanaOrderByPosicionEjercicio(usuario, diaSemana).stream()
                .map(EntrenamientoDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<EntrenamientoDTO> findAll() {
        return entrenamientoRepository.findAll().stream()
                .map(EntrenamientoDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public EntrenamientoDTO findById(Integer id) {
        Entrenamiento e = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrenamiento no encontrado"));
        return new EntrenamientoDTO(e);
    }

    public void deleteById(Integer id) {
        if (!entrenamientoRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrenamiento no encontrado");
        entrenamientoRepository.deleteById(id);
    }

    public EntrenamientoDTO update(Integer id, EntrenamientoDTO dto) {
        Entrenamiento entidad = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrenamiento no encontrado"));

        if (dto.getNombre() != null)
            entidad.setNombre(dto.getNombre());
        if (dto.getDiaSemana() != null)
            entidad.setDiaSemana(dto.getDiaSemana());
        if (dto.getSeries() != null)
            entidad.setSeries(dto.getSeries());
        if (dto.getRepeticiones() != null)
            entidad.setRepeticiones(dto.getRepeticiones());
        if (dto.getPosicionEjercicio() != null)
            entidad.setPosicionEjercicio(dto.getPosicionEjercicio());

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            entidad.setUsuario(usuario);
        }

        Entrenamiento guardado = entrenamientoRepository.save(entidad);
        return new EntrenamientoDTO(guardado);
    }

    // Método de mapeo interno (Antes en Fábrica)
    private Entrenamiento convertirAEntidad(EntrenamientoDTO dto) {
        Entrenamiento e = new Entrenamiento();
        e.setNombre(dto.getNombre());
        e.setDiaSemana(dto.getDiaSemana());
        e.setPosicionEjercicio(dto.getPosicionEjercicio());
        e.setSeries(dto.getSeries());
        e.setRepeticiones(dto.getRepeticiones());
        return e;
    }
}
