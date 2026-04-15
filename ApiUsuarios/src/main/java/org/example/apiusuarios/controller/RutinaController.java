package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.RutinaDTO;
import org.example.apiusuarios.service.RutinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/rutinas")
public class RutinaController {

    @Autowired
    private RutinaService rutinaService;

    @PostMapping("/save")
    public ResponseEntity<RutinaDTO> save(@RequestBody RutinaDTO dto) {
        return new ResponseEntity<>(rutinaService.save(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RutinaDTO>> findAll(@RequestParam(required = false) Integer usuarioId) {
        if (usuarioId != null) return ResponseEntity.ok(rutinaService.findByUsuario(usuarioId));
        return ResponseEntity.ok(rutinaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutinaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(rutinaService.findById(id));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<RutinaDTO> update(@PathVariable Integer id, @RequestBody RutinaDTO dto) {
        return ResponseEntity.ok(rutinaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        rutinaService.deleteById(id);
        return ResponseEntity.ok("Rutina con ID " + id + " eliminada");
    }
}
