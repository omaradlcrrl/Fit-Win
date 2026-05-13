package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.SesionEntrenamientoDTO;
import org.example.apiusuarios.service.SesionEntrenamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/sesiones")
public class SesionEntrenamientoController {

    @Autowired
    private SesionEntrenamientoService sesionService;

    @PostMapping("/iniciar")
    public ResponseEntity<SesionEntrenamientoDTO> iniciar(@RequestBody SesionEntrenamientoDTO sesionEntrenamientoDTO) {
        return new ResponseEntity<>(sesionService.iniciar(sesionEntrenamientoDTO), HttpStatus.CREATED);
    }

    @PutMapping("/finalizar/{id}")
    public ResponseEntity<SesionEntrenamientoDTO> finalizar(@PathVariable Integer id,
                                                             @RequestBody SesionEntrenamientoDTO sesionEntrenamientoDTO) {
        return new ResponseEntity<>(sesionService.finalizar(id, sesionEntrenamientoDTO), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SesionEntrenamientoDTO>> findByUsuario(@RequestParam Integer usuarioId) {
        return new ResponseEntity<>(sesionService.findByUsuario(usuarioId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SesionEntrenamientoDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(sesionService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        sesionService.deleteById(id);
        return new ResponseEntity<>("Sesión con ID " + id + " eliminada exitosamente", HttpStatus.OK);
    }
}
