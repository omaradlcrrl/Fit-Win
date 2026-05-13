package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.FotoProgresoDTO;
import org.example.apiusuarios.service.FotoProgresoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/fotos-progreso")
public class FotoProgresoController {

    @Autowired
    private FotoProgresoService fotoService;

    @PostMapping("/save")
    public ResponseEntity<FotoProgresoDTO> save(@RequestBody FotoProgresoDTO fotoProgresoDTO) {
        return new ResponseEntity<>(fotoService.save(fotoProgresoDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FotoProgresoDTO>> findByUsuario(@RequestParam Integer usuarioId) {
        return new ResponseEntity<>(fotoService.findByUsuario(usuarioId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        fotoService.deleteById(id);
        return new ResponseEntity<>("Foto con ID " + id + " eliminada exitosamente", HttpStatus.OK);
    }
}
