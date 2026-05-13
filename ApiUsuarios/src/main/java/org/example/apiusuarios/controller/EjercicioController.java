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
    public ResponseEntity<EjercicioDTO> save(@RequestBody EjercicioDTO ejercicioDTO) {
        return new ResponseEntity<>(ejercicioService.save(ejercicioDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EjercicioDTO>> findAll(
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) String diaSemana,
            @RequestParam(required = false) Integer rutinaId) {
        if (usuarioId != null && diaSemana != null)
            return new ResponseEntity<>(ejercicioService.findByUsuarioAndDia(usuarioId, diaSemana), HttpStatus.OK);
        if (rutinaId != null)
            return new ResponseEntity<>(ejercicioService.findByRutina(rutinaId), HttpStatus.OK);
        return new ResponseEntity<>(ejercicioService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjercicioDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(ejercicioService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<EjercicioDTO> update(@PathVariable Integer id, @RequestBody EjercicioDTO ejercicioDTO) {
        return new ResponseEntity<>(ejercicioService.update(id, ejercicioDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        ejercicioService.deleteById(id);
        return new ResponseEntity<>("Ejercicio con ID " + id + " eliminado exitosamente", HttpStatus.OK);
    }
}
