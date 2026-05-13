package org.example.apiusuarios.dto.login;

import lombok.Data;

@Data
public class RefreshResponse {
    private String token;
    private String refreshToken;
}
