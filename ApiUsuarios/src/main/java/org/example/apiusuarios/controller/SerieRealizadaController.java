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
    public ResponseEntity<SerieRealizadaDTO> save(@RequestBody SerieRealizadaDTO serieRealizadaDTO) {
        return new ResponseEntity<>(serieService.save(serieRealizadaDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SerieRealizadaDTO>> findBySesion(@RequestParam Integer sesionId) {
        return new ResponseEntity<>(serieService.findBySesion(sesionId), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<SerieRealizadaDTO> update(@PathVariable Integer id,
                                                     @RequestBody SerieRealizadaDTO serieRealizadaDTO) {
        return new ResponseEntity<>(serieService.update(id, serieRealizadaDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        serieService.deleteById(id);
        return new ResponseEntity<>("Serie con ID " + id + " eliminada exitosamente", HttpStatus.OK);
    }
}
