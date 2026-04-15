package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.ComidaDTO;
import org.example.apiusuarios.model.Comida;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.ComidaRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ComidaService {

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ComidaDTO save(ComidaDTO comidaDTO) {
        if (comidaDTO == null || comidaDTO.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(comidaDTO.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Comida comida = convertirAEntidad(comidaDTO);
        comida.setUsuario(usuario);

        Comida guardada = comidaRepository.save(comida);
        return new ComidaDTO(guardada);
    }

    public void deleteByNombre(Integer usuarioId, String nombre) {
        if (usuarioId == null || nombre == null || nombre.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId y nombre son obligatorios");
        }

        LocalDate hoy = LocalDate.now();
        List<Comida> comidas = comidaRepository.findByUsuario_UsuarioIdAndFecha(usuarioId, hoy);

        boolean eliminado = false;
        for (Comida c : comidas) {
            if (c.getNombre().equalsIgnoreCase(nombre.trim())) {
                comidaRepository.delete(c);
                eliminado = true;
                break;
            }
        }

        if (!eliminado) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la comida con ese nombre hoy");
        }
    }

    public List<ComidaDTO> findAll() {
        return comidaRepository.findAll().stream()
                .map(ComidaDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public ComidaDTO findById(Integer id) {
        Comida comida = comidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comida no encontrada"));
        return new ComidaDTO(comida);
    }

    public List<ComidaDTO> findByUsuarioHoy(Integer usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");
        }
        LocalDate hoy = LocalDate.now();
        List<Comida> comidas = comidaRepository.findByUsuario_UsuarioIdAndFecha(usuarioId, hoy);
        return comidas.stream()
                .map(ComidaDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ComidaDTO> findByUsuario(Integer usuarioId) {
        return comidaRepository.findByUsuario_UsuarioIdOrderByFechaDesc(usuarioId)
                .stream().map(ComidaDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ComidaDTO> findByUsuarioAndFecha(Integer usuarioId, LocalDate fecha) {
        return comidaRepository.findByUsuario_UsuarioIdAndFecha(usuarioId, fecha)
                .stream().map(ComidaDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ComidaDTO> findByUsuarioAndRango(Integer usuarioId, LocalDate from, LocalDate to) {
        return comidaRepository.findByUsuario_UsuarioIdAndFechaBetweenOrderByFechaAsc(usuarioId, from, to)
                .stream().map(ComidaDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public void deleteById(Integer id) {
        if (!comidaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comida no encontrada");
        }
        comidaRepository.deleteById(id);
    }

    public ComidaDTO update(Integer id, ComidaDTO dto) {
        Comida entidad = comidaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comida no encontrada"));

        if (dto.getNombre() != null)
            entidad.setNombre(dto.getNombre());
        if (dto.getCalorias() != null)
            entidad.setCalorias(dto.getCalorias());
        if (dto.getGrasasSaturadas() != null)
            entidad.setGrasasSaturadas(dto.getGrasasSaturadas());
        if (dto.getProteinas() != null)
            entidad.setProteinas(dto.getProteinas());
        if (dto.getCarbohidratos() != null)
            entidad.setCarbohidratos(dto.getCarbohidratos());
        if (dto.getFecha() != null)
            entidad.setFecha(dto.getFecha());
        if (dto.getTipoComida() != null)
            entidad.setTipoComida(dto.getTipoComida());
        if (dto.getCantidad() != null)
            entidad.setCantidad(dto.getCantidad());
        if (dto.getUnidad() != null)
            entidad.setUnidad(dto.getUnidad());

        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            entidad.setUsuario(usuario);
        }

        Comida guardada = comidaRepository.save(entidad);
        return new ComidaDTO(guardada);
    }

    // Método de mapeo interno (Antes en Fábrica)
    private Comida convertirAEntidad(ComidaDTO dto) {
        Comida c = new Comida();
        c.setNombre(dto.getNombre());
        c.setCalorias(dto.getCalorias());
        c.setGrasasSaturadas(dto.getGrasasSaturadas());
        c.setCarbohidratos(dto.getCarbohidratos());
        c.setProteinas(dto.getProteinas());
        c.setTipoComida(dto.getTipoComida());
        c.setCantidad(dto.getCantidad());
        c.setUnidad(dto.getUnidad());
        c.setFecha(dto.getFecha());
        return c;
    }
}