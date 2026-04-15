package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.EjercicioDTO;
import org.example.apiusuarios.service.EjercicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/ejercicios")
public class EjercicioController {

    @Autowired
    private EjercicioService ejercicioService;

    @PostMapping("/save")
    public ResponseEntity<EjercicioDTO> save(@RequestBody EjercicioDTO dto) {
        return new ResponseEntity<>(ejercicioService.save(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EjercicioDTO>> findAll(
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) String diaSemana,
            @RequestParam(required = false) Integer rutinaId) {
        if (usuarioId != null && diaSemana != null)
            return ResponseEntity.ok(ejercicioService.findByUsuarioAndDia(usuarioId, diaSemana));
        if (rutinaId != null)
            return ResponseEntity.ok(ejercicioService.findByRutina(rutinaId));
        return ResponseEntity.ok(ejercicioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjercicioDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(ejercicioService.findById(id));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<EjercicioDTO> update(@PathVariable Integer id, @RequestBody EjercicioDTO dto) {
        return ResponseEntity.ok(ejercicioService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        ejercicioService.deleteById(id);
        return ResponseEntity.ok("Ejercicio con ID " + id + " eliminado");
    }
}
