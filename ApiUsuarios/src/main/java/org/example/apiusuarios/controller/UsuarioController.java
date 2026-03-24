package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.UsuarioDTO;
import org.example.apiusuarios.dto.login.LoginRequest;
import org.example.apiusuarios.dto.login.LoginResponse;
import org.example.apiusuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/FWBBD/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/save")
    public ResponseEntity<UsuarioDTO> save(@RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.save(usuarioDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(usuarioService.login(loginRequest), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        return new ResponseEntity<>(usuarioService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Integer id) {
        return new ResponseEntity<>(usuarioService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Integer id) {
        usuarioService.deleteById(id);
        return new ResponseEntity<>("Usuario con ID " + id + " eliminado exitosamente", HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Integer id,
                                             @RequestBody UsuarioDTO usuarioDTO) {
        return new ResponseEntity<>(usuarioService.update(id, usuarioDTO), HttpStatus.OK);
    }
}

