package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.EjercicioGlobalDTO;
import org.example.apiusuarios.model.CategoriaEjercicio;
import org.example.apiusuarios.service.EjercicioGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/ejercicios-globales")
public class EjercicioGlobalController {

    @Autowired
    private EjercicioGlobalService service;

    @PostMapping("/save")
    public ResponseEntity<EjercicioGlobalDTO> save(@RequestBody EjercicioGlobalDTO dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EjercicioGlobalDTO>> findAll(
            @RequestParam(required = false) CategoriaEjercicio categoria,
            @RequestParam(required = false) String buscar) {
        if (categoria != null) return ResponseEntity.ok(service.findByCategoria(categoria));
        if (buscar != null) return ResponseEntity.ok(service.search(buscar));
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjercicioGlobalDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<EjercicioGlobalDTO> update(@PathVariable Integer id, @RequestBody EjercicioGlobalDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        service.deleteById(id);
        return ResponseEntity.ok("Ejercicio global con ID " + id + " eliminado");
    }
}
