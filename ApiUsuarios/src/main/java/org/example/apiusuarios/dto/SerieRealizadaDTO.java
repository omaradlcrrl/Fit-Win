package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.SerieRealizada;

@Data
@NoArgsConstructor
public class SerieRealizadaDTO {

    private Integer serieId;
    private Integer sesionId;
    private Integer ejercicioId;
    private String nombreEjercicio;
    private Double pesoKg;
    private Integer repeticionesRealizadas;
    private Boolean completado;
    private Integer orden;

    public SerieRealizadaDTO(SerieRealizada s) {
        this.serieId = s.getSerieId();
        this.sesionId = s.getSesion() != null ? s.getSesion().getSesionId() : null;
        this.ejercicioId = s.getEjercicio() != null ? s.getEjercicio().getEjercicioId() : null;
        this.nombreEjercicio = s.getEjercicio() != null && s.getEjercicio().getEjercicioGlobal() != null
                ? s.getEjercicio().getEjercicioGlobal().getNombre() : null;
        this.pesoKg = s.getPesoKg();
        this.repeticionesRealizadas = s.getRepeticionesRealizadas();
        this.completado = s.getCompletado();
        this.orden = s.getOrden();
    }
}
