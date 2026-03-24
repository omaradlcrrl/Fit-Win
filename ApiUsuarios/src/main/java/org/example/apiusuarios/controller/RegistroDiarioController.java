package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.RegistroDiarioDTO;
import org.example.apiusuarios.service.RegistroDiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/registrosdiarios")
public class RegistroDiarioController {

    @Autowired
    private RegistroDiarioService registroDiarioService;

    @PostMapping("/save")
    public ResponseEntity<RegistroDiarioDTO> save(@RequestBody RegistroDiarioDTO registroDiarioDTO) {
        RegistroDiarioDTO resultado = registroDiarioService.save(registroDiarioDTO);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RegistroDiarioDTO>> findAll() {
        return new ResponseEntity<>(registroDiarioService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/hoy/{usuarioId}")
    public ResponseEntity<RegistroDiarioDTO> getHoy(@PathVariable Integer usuarioId) {
        return new ResponseEntity<>(registroDiarioService.findByUsuarioHoy(usuarioId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroDiarioDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(registroDiarioService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        registroDiarioService.deleteById(id);
        return new ResponseEntity<>("Registro con ID " + id + " eliminado exitosamente", HttpStatus.OK);
    }

    @GetMapping("/range/{usuarioId}")
    public ResponseEntity<List<RegistroDiarioDTO>> getByRange(
            @PathVariable Integer usuarioId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        List<RegistroDiarioDTO> lista = registroDiarioService.findByUsuarioRange(usuarioId, from, to);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }
}