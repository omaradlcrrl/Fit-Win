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
    private EjercicioGlobalService ejercicioGlobalService;

    @PostMapping("/save")
    public ResponseEntity<EjercicioGlobalDTO> save(@RequestBody EjercicioGlobalDTO ejercicioGlobalDTO) {
        return new ResponseEntity<>(ejercicioGlobalService.save(ejercicioGlobalDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EjercicioGlobalDTO>> findAll(
            @RequestParam(required = false) CategoriaEjercicio categoria,
            @RequestParam(required = false) String buscar) {
        if (categoria != null) return new ResponseEntity<>(ejercicioGlobalService.findByCategoria(categoria), HttpStatus.OK);
        if (buscar != null) return new ResponseEntity<>(ejercicioGlobalService.search(buscar), HttpStatus.OK);
        return new ResponseEntity<>(ejercicioGlobalService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjercicioGlobalDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(ejercicioGlobalService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<EjercicioGlobalDTO> update(@PathVariable Integer id, @RequestBody EjercicioGlobalDTO ejercicioGlobalDTO) {
        return new ResponseEntity<>(ejercicioGlobalService.update(id, ejercicioGlobalDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        ejercicioGlobalService.deleteById(id);
        return new ResponseEntity<>("Ejercicio global con ID " + id + " eliminado exitosamente", HttpStatus.OK);
    }
}
