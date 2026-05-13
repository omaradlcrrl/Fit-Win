package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.RecordPersonalDTO;
import org.example.apiusuarios.service.RecordPersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/records")
public class RecordPersonalController {

    @Autowired
    private RecordPersonalService recordService;

    @PostMapping("/save")
    public ResponseEntity<RecordPersonalDTO> save(@RequestBody RecordPersonalDTO recordPersonalDTO) {
        return new ResponseEntity<>(recordService.save(recordPersonalDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RecordPersonalDTO>> findByUsuario(@RequestParam Integer usuarioId) {
        return new ResponseEntity<>(recordService.findByUsuario(usuarioId), HttpStatus.OK);
    }

    @GetMapping("/ejercicio")
    public ResponseEntity<List<RecordPersonalDTO>> findByUsuarioAndEjercicio(
            @RequestParam Integer usuarioId,
            @RequestParam Integer ejercicioGlobalId) {
        return new ResponseEntity<>(recordService.findByUsuarioAndEjercicio(usuarioId, ejercicioGlobalId), HttpStatus.OK);
    }

    @GetMapping("/ejercicio/max")
    public ResponseEntity<RecordPersonalDTO> findMaxByUsuarioAndEjercicio(
            @RequestParam Integer usuarioId,
            @RequestParam Integer ejercicioGlobalId) {
        return new ResponseEntity<>(recordService.findMaxByUsuarioAndEjercicio(usuarioId, ejercicioGlobalId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordPersonalDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(recordService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        recordService.deleteById(id);
        return new ResponseEntity<>("Record con ID " + id + " eliminado exitosamente", HttpStatus.OK);
    }
}
