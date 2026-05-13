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
    public ResponseEntity<RutinaDTO> save(@RequestBody RutinaDTO rutinaDTO) {
        return new ResponseEntity<>(rutinaService.save(rutinaDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RutinaDTO>> findAll(@RequestParam(required = false) Integer usuarioId) {
        if (usuarioId != null) return new ResponseEntity<>(rutinaService.findByUsuario(usuarioId), HttpStatus.OK);
        return new ResponseEntity<>(rutinaService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutinaDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(rutinaService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<RutinaDTO> update(@PathVariable Integer id, @RequestBody RutinaDTO rutinaDTO) {
        return new ResponseEntity<>(rutinaService.update(id, rutinaDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        rutinaService.deleteById(id);
        return new ResponseEntity<>("Rutina con ID " + id + " eliminada exitosamente", HttpStatus.OK);
    }
}
