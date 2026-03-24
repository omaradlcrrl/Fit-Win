package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.RegistroDiario;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RegistroDiarioDTO {

    private Integer registroId;
    private LocalDate fecha;
    private Double peso;
    private Integer usuarioId;

    public RegistroDiarioDTO(RegistroDiario r) {
        this.registroId = r.getRegistroId();
        this.fecha = r.getFecha();
        this.peso = r.getPeso();
        if (r.getUsuario() != null) {
            this.usuarioId = r.getUsuario().getUsuarioId();
        }
    }
}
