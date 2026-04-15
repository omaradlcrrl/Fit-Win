package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.SerieRealizadaDTO;
import org.example.apiusuarios.service.SerieRealizadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/series")
public class SerieRealizadaController {

    @Autowired
    private SerieRealizadaService serieService;

    @PostMapping("/save")
    public ResponseEntity<SerieRealizadaDTO> save(@RequestBody SerieRealizadaDTO dto) {
        return new ResponseEntity<>(serieService.save(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SerieRealizadaDTO>> findBySesion(@RequestParam Integer sesionId) {
        return ResponseEntity.ok(serieService.findBySesion(sesionId));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<SerieRealizadaDTO> update(@PathVariable Integer id,
                                                     @RequestBody SerieRealizadaDTO dto) {
        return ResponseEntity.ok(serieService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        serieService.deleteById(id);
        return ResponseEntity.ok("Serie con ID " + id + " eliminada");
    }
}
