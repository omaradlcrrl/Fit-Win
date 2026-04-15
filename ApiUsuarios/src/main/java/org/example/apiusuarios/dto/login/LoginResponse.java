package org.example.apiusuarios.dto.login;

import lombok.Data;

@Data
public class LoginResponse {
    private String mensaje;
    private String token;
    private String refreshToken;
    private Integer usuarioId;
}
