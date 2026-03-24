package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.MedicionCorporalDTO;
import org.example.apiusuarios.service.MedicionCorporalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/mediciones")
public class MedicionCorporalController {

    @Autowired
    private MedicionCorporalService medicionService;

    @PostMapping("/save")
    public ResponseEntity<MedicionCorporalDTO> save(@RequestBody MedicionCorporalDTO dto) {
        MedicionCorporalDTO resultado = medicionService.save(dto);
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MedicionCorporalDTO>> findAll() {
        return new ResponseEntity<>(medicionService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicionCorporalDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(medicionService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MedicionCorporalDTO>> getByUsuario(@PathVariable Integer usuarioId) {
        return new ResponseEntity<>(medicionService.findByUsuario(usuarioId), HttpStatus.OK);
    }

    @GetMapping("/ultima/{usuarioId}")
    public ResponseEntity<MedicionCorporalDTO> getByUltima(@PathVariable Integer usuarioId) {
        return new ResponseEntity<>(medicionService.findUltima(usuarioId), HttpStatus.OK);
    }

    @GetMapping("/range/{usuarioId}")
    public ResponseEntity<List<MedicionCorporalDTO>> findByRange(
            @PathVariable Integer usuarioId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return new ResponseEntity<>(medicionService.findByUsuarioAndRango(usuarioId, from, to), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        medicionService.deleteById(id);
        return new ResponseEntity<>("Medición con ID " + id + " eliminada exitosamente", HttpStatus.OK);
    }

    @DeleteMapping("/deleteHoy/{usuarioId}")
    public ResponseEntity<String> deleteHoy(@PathVariable Integer usuarioId) {
        boolean borrada = medicionService.deleteHoy(usuarioId);
        LocalDate hoy = LocalDate.now();
        if (borrada) {
            return new ResponseEntity<>("Medición de hoy (" + hoy + ") eliminada correctamente para el usuario " + usuarioId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No existe medición de hoy (" + hoy + ") para el usuario " + usuarioId, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<MedicionCorporalDTO> update(
            @PathVariable Integer id,
            @RequestBody MedicionCorporalDTO dto
    ) {
        MedicionCorporalDTO resultado = medicionService.update(id, dto);
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
}