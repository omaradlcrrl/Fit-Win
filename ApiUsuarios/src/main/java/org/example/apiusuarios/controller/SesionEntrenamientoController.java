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
    public ResponseEntity<SesionEntrenamientoDTO> iniciar(@RequestBody SesionEntrenamientoDTO dto) {
        return new ResponseEntity<>(sesionService.iniciar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/finalizar/{id}")
    public ResponseEntity<SesionEntrenamientoDTO> finalizar(@PathVariable Integer id,
                                                             @RequestBody SesionEntrenamientoDTO dto) {
        return ResponseEntity.ok(sesionService.finalizar(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<SesionEntrenamientoDTO>> findByUsuario(@RequestParam Integer usuarioId) {
        return ResponseEntity.ok(sesionService.findByUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SesionEntrenamientoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(sesionService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        sesionService.deleteById(id);
        return ResponseEntity.ok("Sesión con ID " + id + " eliminada");
    }
}
