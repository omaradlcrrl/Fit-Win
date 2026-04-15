package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.RecordPersonal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RecordPersonalDTO {

    private Integer recordId;
    private Integer ejercicioGlobalId;
    private String nombreEjercicio;
    private Integer usuarioId;
    private Double pesoKg;
    private LocalDate fecha;

    public RecordPersonalDTO(RecordPersonal r) {
        this.recordId = r.getRecordId();
        this.ejercicioGlobalId = r.getEjercicioGlobal() != null ? r.getEjercicioGlobal().getEjercicioGlobalId() : null;
        this.nombreEjercicio = r.getEjercicioGlobal() != null ? r.getEjercicioGlobal().getNombre() : null;
        this.usuarioId = r.getUsuario() != null ? r.getUsuario().getUsuarioId() : null;
        this.pesoKg = r.getPesoKg();
        this.fecha = r.getFecha();
    }
}
