package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.MedicionCorporal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MedicionCorporalDTO {

    private Integer medicionId;
    private Integer usuarioId;
    private LocalDate fecha;
    private Double pecho;
    private Double espalda;
    private Double brazo;
    private Double muslo;
    private Double hombro;

    public MedicionCorporalDTO(MedicionCorporal m) {
        this.medicionId = m.getMedicionId();
        this.usuarioId = (m.getUsuario() != null) ? m.getUsuario().getUsuarioId() : null;
        this.fecha = m.getFecha();
        this.pecho = m.getPecho();
        this.espalda = m.getEspalda();
        this.brazo = m.getBrazo();
        this.muslo = m.getMuslo();
        this.hombro = m.getHombro();

    }
}
