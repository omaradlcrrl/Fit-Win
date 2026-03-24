package org.example.apiusuarios.service;

import org.example.apiusuarios.dto.RegistroDiarioDTO;
import org.example.apiusuarios.model.RegistroDiario;
import org.example.apiusuarios.model.Usuario;
import org.example.apiusuarios.repository.RegistroDiarioRepository;
import org.example.apiusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@Service
public class RegistroDiarioService {

    @Autowired
    private RegistroDiarioRepository registroDiarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public RegistroDiarioDTO save(RegistroDiarioDTO dto) {
        if (dto.getUsuarioId() == null || dto.getPeso() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario ID y Peso son obligatorios");
        }

        LocalDate hoy = LocalDate.now();

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        RegistroDiario registro = registroDiarioRepository
                .findByUsuario_UsuarioIdAndFecha(dto.getUsuarioId(), hoy)
                .orElse(new RegistroDiario());

        if (registro.getRegistroId() == null) {
            registro.setUsuario(usuario);
            registro.setFecha(hoy);
        }

        registro.setPeso(dto.getPeso());

        RegistroDiario guardado = registroDiarioRepository.save(registro);
        return new RegistroDiarioDTO(guardado);
    }

    public List<RegistroDiarioDTO> findAll() {
        return registroDiarioRepository.findAll().stream()
                .map(RegistroDiarioDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

    public RegistroDiarioDTO findById(Integer id) {
        RegistroDiario reg = registroDiarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
        return new RegistroDiarioDTO(reg);
    }

    public RegistroDiarioDTO findByUsuarioHoy(Integer usuarioId) {
        LocalDate hoy = LocalDate.now();
        RegistroDiario reg = registroDiarioRepository
                .findByUsuario_UsuarioIdAndFecha(usuarioId, hoy)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sin registro hoy"));
        return new RegistroDiarioDTO(reg);
    }

    public void deleteById(Integer id) {
        registroDiarioRepository.deleteById(id);
    }

    public List<RegistroDiarioDTO> findByUsuarioRange(Integer usuarioId, LocalDate from, LocalDate to) {
        if (usuarioId == null || from == null || to == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioid, from y to son obligatorios");
        }
        if (from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from no puede ser posterior a to");
        }

        List<RegistroDiario> registros = registroDiarioRepository
                .findByUsuario_UsuarioIdAndFechaBetweenOrderByFechaAsc(usuarioId, from, to);

        return registros.stream()
                .map(RegistroDiarioDTO::new)
                .collect(java.util.stream.Collectors.toList());
    }

}