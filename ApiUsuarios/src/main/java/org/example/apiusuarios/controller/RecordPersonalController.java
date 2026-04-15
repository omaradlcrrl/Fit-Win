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
    public ResponseEntity<RecordPersonalDTO> save(@RequestBody RecordPersonalDTO dto) {
        return new ResponseEntity<>(recordService.save(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RecordPersonalDTO>> findByUsuario(@RequestParam Integer usuarioId) {
        return ResponseEntity.ok(recordService.findByUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordPersonalDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(recordService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        recordService.deleteById(id);
        return ResponseEntity.ok("Record con ID " + id + " eliminado");
    }
}
