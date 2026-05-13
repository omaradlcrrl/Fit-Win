package org.example.apiusuarios.controller;

import org.example.apiusuarios.dto.login.RefreshRequest;
import org.example.apiusuarios.dto.login.RefreshResponse;
import org.example.apiusuarios.model.RefreshToken;
import org.example.apiusuarios.security.JwtService;
import org.example.apiusuarios.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/FWBBD/auth")
public class AuthController {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest request) {
        RefreshToken rt = refreshTokenService.verificar(request.getRefreshToken());

        var userDetails = userDetailsService.loadUserByUsername(
                rt.getUsuario().getCorreoElectronico());
        String nuevoJwt = jwtService.generateToken(userDetails);

        refreshTokenService.revocarPorUsuario(rt.getUsuario().getUsuarioId());
        RefreshToken nuevoRt = refreshTokenService.crear(rt.getUsuario().getUsuarioId());

        RefreshResponse response = new RefreshResponse();
        response.setToken(nuevoJwt);
        response.setRefreshToken(nuevoRt.getToken());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam Integer usuarioId) {
        refreshTokenService.revocarPorUsuario(usuarioId);
        return new ResponseEntity<>("Sesión cerrada correctamente", HttpStatus.OK);
    }
}
