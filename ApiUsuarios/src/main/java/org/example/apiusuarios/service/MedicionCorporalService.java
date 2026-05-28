package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.MedicionCorporalDTO;
import org.example.apiusuarios.model.MedicionCorporal;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.MedicionCorporalRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.example.apiusuarios.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicionCorporalService {

    @Autowired
    private MedicionCorporalRepository medicionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public MedicionCorporalDTO save(MedicionCorporalDTO dto) {
        if (dto.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");
        }
        securityUtils.assertEsDuenoOAdmin(dto.getUsuarioId());

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        LocalDate fecha = (dto.getFecha() != null) ? dto.getFecha() : LocalDate.now();

        boolean yaExiste = medicionRepository.existsByUsuario_UsuarioIdAndFecha(dto.getUsuarioId(), fecha);
        if (yaExiste) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una medición para el usuario " + dto.getUsuarioId() + " en la fecha " + fecha);
        }

        MedicionCorporal m = convertirAEntidad(dto);
        m.setUsuario(usuario);
        m.setFecha(fecha);

        MedicionCorporal guardada = medicionRepository.save(m);
        return new MedicionCorporalDTO(guardada);
    }

    public MedicionCorporalDTO update(Integer id, MedicionCorporalDTO dto) {
        MedicionCorporal m = medicionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medición no encontrada"));
        securityUtils.assertEsDuenoOAdmin(m.getUsuario().getUsuarioId());

        if (dto.getUsuarioId() != null && !dto.getUsuarioId().equals(m.getUsuario().getUsuarioId())) {
            if (!securityUtils.esAdmin()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes reasignar el dueño");
            }
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            m.setUsuario(usuario);
        }

        if (dto.getFecha() != null) {
            m.setFecha(dto.getFecha());
        }

        if (dto.getPeso() != null)
            m.setPeso(dto.getPeso());
        if (dto.getPorcentajeGrasa() != null)
            m.setPorcentajeGrasa(dto.getPorcentajeGrasa());
        if (dto.getMasaMagra() != null)
            m.setMasaMagra(dto.getMasaMagra());
        if (dto.getCintura() != null)
            m.setCintura(dto.getCintura());
        if (dto.getPecho() != null)
            m.setPecho(dto.getPecho());
        if (dto.getEspalda() != null)
            m.setEspalda(dto.getEspalda());
        if (dto.getHombro() != null)
            m.setHombro(dto.getHombro());
        if (dto.getBrazo() != null)
            m.setBrazo(dto.getBrazo());
        if (dto.getMuslo() != null)
            m.setMuslo(dto.getMuslo());

        MedicionCorporal actualizada = medicionRepository.save(m);
        return new MedicionCorporalDTO(actualizada);
    }

    public List<MedicionCorporalDTO> findAll() {
        securityUtils.assertEsDuenoOAdmin(-1);
        return medicionRepository.findAll().stream()
                .map(MedicionCorporalDTO::new)
                .collect(Collectors.toList());
    }

    public MedicionCorporalDTO findById(Integer id) {
        MedicionCorporal m = medicionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medición no encontrada"));
        securityUtils.assertEsDuenoOAdmin(m.getUsuario().getUsuarioId());
        return new MedicionCorporalDTO(m);
    }

    public void deleteById(Integer id) {
        MedicionCorporal m = medicionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medición no encontrada"));
        securityUtils.assertEsDuenoOAdmin(m.getUsuario().getUsuarioId());
        medicionRepository.delete(m);
    }

    public List<MedicionCorporalDTO> findByUsuario(Integer usuarioId) {
        securityUtils.assertEsDuenoOAdmin(usuarioId);
        return medicionRepository.findByUsuario_UsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(MedicionCorporalDTO::new)
                .collect(Collectors.toList());
    }

    public MedicionCorporalDTO findUltima(Integer usuarioId) {
        securityUtils.assertEsDuenoOAdmin(usuarioId);
        MedicionCorporal ultima = medicionRepository.findFirstByUsuario_UsuarioIdOrderByFechaDesc(usuarioId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sin mediciones para este usuario"));
        return new MedicionCorporalDTO(ultima);
    }

    public List<MedicionCorporalDTO> findByUsuarioAndRango(Integer usuarioId, LocalDate from, LocalDate to) {
        securityUtils.assertEsDuenoOAdmin(usuarioId);
        return medicionRepository.findByUsuario_UsuarioIdAndFechaBetweenOrderByFechaAsc(usuarioId, from, to).stream()
                .map(MedicionCorporalDTO::new)
                .collect(Collectors.toList());
    }

    public boolean deleteHoy(Integer usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es obligatorio");
        }
        securityUtils.assertEsDuenoOAdmin(usuarioId);
        LocalDate hoy = LocalDate.now();
        return medicionRepository.findByUsuario_UsuarioIdAndFecha(usuarioId, hoy)
                .map(m -> {
                    medicionRepository.delete(m);
                    return true;
                })
                .orElse(false);
    }

    private MedicionCorporal convertirAEntidad(MedicionCorporalDTO dto) {
        MedicionCorporal m = new MedicionCorporal();
        m.setPeso(dto.getPeso());
        m.setPorcentajeGrasa(dto.getPorcentajeGrasa());
        m.setMasaMagra(dto.getMasaMagra());
        m.setCintura(dto.getCintura());
        m.setPecho(dto.getPecho());
        m.setEspalda(dto.getEspalda());
        m.setBrazo(dto.getBrazo());
        m.setMuslo(dto.getMuslo());
        m.setHombro(dto.getHombro());
        return m;
    }
}