package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.ObjetivoDTO;
import org.example.apiusuarios.service.ObjetivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/objetivos")
public class ObjetivoController {

    @Autowired
    private ObjetivoService objetivoService;


    @PostMapping("/save")
    public ResponseEntity<ObjetivoDTO> save(@RequestBody ObjetivoDTO dto) {
        if (dto.getUsuarioId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ObjetivoDTO generado = objetivoService.generarAutomatico(dto.getUsuarioId());
        return new ResponseEntity<>(generado, HttpStatus.CREATED);
    }

    @PostMapping("/generar/{usuarioId}")
    public ResponseEntity<ObjetivoDTO> saveAutomatico(@PathVariable Integer usuarioId) {
        return new ResponseEntity<>(objetivoService.generarAutomatico(usuarioId), HttpStatus.CREATED);
    }

    @GetMapping("/actual/{usuarioId}")
    public ResponseEntity<ObjetivoDTO> getActual(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(objetivoService.getObjetivoActual(usuarioId));
    }

    @GetMapping("/hoy/{usuarioId}")
    public ResponseEntity<ObjetivoDTO> getHoy(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(objetivoService.getObjetivoDeHoy(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ObjetivoDTO>> getByUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(objetivoService.findAllByUsuario(usuarioId));
    }

    @GetMapping("/range/{usuarioId}")
    public ResponseEntity<List<ObjetivoDTO>> getByRange(@PathVariable Integer usuarioId,
                                                        @RequestParam LocalDate from,
                                                        @RequestParam LocalDate to) {
        return ResponseEntity.ok(objetivoService.getByUsuarioAndFechaBetween(usuarioId, from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(objetivoService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        objetivoService.deleteById(id);
        return ResponseEntity.ok("Objetivo con ID " + id + " eliminado exitosamente");
    }
}