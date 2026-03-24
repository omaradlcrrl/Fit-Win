package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.EntrenamientoDTO;
import org.example.apiusuarios.service.EntrenamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/entrenamientos")
public class EntrenamientoController {


    @Autowired
    private EntrenamientoService entrenamientoService;


    @PostMapping("/save")
    public ResponseEntity<EntrenamientoDTO> save(@RequestBody EntrenamientoDTO dto) {
        return new ResponseEntity<>(entrenamientoService.save(dto), HttpStatus.CREATED);
    }

    @GetMapping("/findByUserAndDay")
    public ResponseEntity<List<EntrenamientoDTO>> findByUserAndDay(
            @RequestParam Integer usuarioId,
            @RequestParam String diaSemana) {
        List<EntrenamientoDTO> lista =
                entrenamientoService.findByUsuarioAndDiaSemanaOrdered(usuarioId, diaSemana);
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EntrenamientoDTO>> findAll() {
        return new ResponseEntity<>(entrenamientoService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrenamientoDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(entrenamientoService.findById(id), HttpStatus.OK);
    }


    @DeleteMapping("/deleteByPosicionYDia")
    public ResponseEntity<String> deleteByPosicionYDia(
            @RequestParam Integer usuarioId,
            @RequestParam String diaSemana,
            @RequestParam Integer posicion
    ) {
        entrenamientoService.deleteByPosicionYDia(usuarioId, diaSemana, posicion);
        return new ResponseEntity<>("Eliminado día=" + diaSemana + " pos=" + posicion + " para usuario " + usuarioId, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        entrenamientoService.deleteById(id);
        return new ResponseEntity<>("Entrenamiento con ID " + id + " eliminado exitosamente", HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<EntrenamientoDTO> update(@PathVariable Integer id, @RequestBody EntrenamientoDTO dto) {
        return new ResponseEntity<>(entrenamientoService.update(id, dto), HttpStatus.OK);
    }
}
