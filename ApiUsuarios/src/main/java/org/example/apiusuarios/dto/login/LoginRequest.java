package org.example.apiusuarios.dto.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String correoElectronico;
    private String password;
}