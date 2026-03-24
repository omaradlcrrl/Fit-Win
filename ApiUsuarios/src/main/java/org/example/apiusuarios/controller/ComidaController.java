package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.ComidaDTO;
import org.example.apiusuarios.service.ComidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/api/v1/FWBBD/comidas")
public class ComidaController {

    @Autowired
    private ComidaService comidaService;

    @PostMapping("/save")
    public ResponseEntity<ComidaDTO> save(@RequestBody ComidaDTO comidaDTO) {
        return new ResponseEntity<>(comidaService.save(comidaDTO), HttpStatus.CREATED);
    }

    @GetMapping("/hoy/{usuarioId}")
    public ResponseEntity<List<ComidaDTO>> getHoy(@PathVariable Integer usuarioId) {
        return new ResponseEntity<>(comidaService.findByUsuarioHoy(usuarioId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ComidaDTO>> findAll() {
        return new ResponseEntity<>(comidaService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComidaDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(comidaService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/deleteByNombre")
    public ResponseEntity<String> deleteByNombre(
            @RequestParam Integer usuarioId,
            @RequestParam String nombre
    ) {
        comidaService.deleteByNombre(usuarioId, nombre);
        return new ResponseEntity<>("Comida '" + nombre + "' eliminada correctamente para el usuario " + usuarioId, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        comidaService.deleteById(id);
        return new ResponseEntity<>("Comida con ID " + id + " eliminada exitosamente", HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ComidaDTO> update(@PathVariable Integer id, @RequestBody ComidaDTO dto) {
        return new ResponseEntity<>(comidaService.update(id, dto), HttpStatus.OK);
    }
}