package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.Ejercicio;

@Data
@NoArgsConstructor
public class EjercicioDTO {

    private Integer ejercicioId;
    private Integer ejercicioGlobalId;
    private String nombreEjercicio;
    private Integer rutinaId;
    private Integer usuarioId;
    private String diaSemana;
    private Integer series;
    private Integer repeticionesMin;
    private Integer repeticionesMax;
    private Integer descansoSegundos;
    private Double pesoKg;
    private Integer posicion;

    public EjercicioDTO(Ejercicio e) {
        this.ejercicioId = e.getEjercicioId();
        this.ejercicioGlobalId = e.getEjercicioGlobal() != null ? e.getEjercicioGlobal().getEjercicioGlobalId() : null;
        this.nombreEjercicio = e.getEjercicioGlobal() != null ? e.getEjercicioGlobal().getNombre() : null;
        this.rutinaId = e.getRutina() != null ? e.getRutina().getRutinaId() : null;
        this.usuarioId = e.getUsuario() != null ? e.getUsuario().getUsuarioId() : null;
        this.diaSemana = e.getDiaSemana();
        this.series = e.getSeries();
        this.repeticionesMin = e.getRepeticionesMin();
        this.repeticionesMax = e.getRepeticionesMax();
        this.descansoSegundos = e.getDescansoSegundos();
        this.pesoKg = e.getPesoKg();
        this.posicion = e.getPosicion();
    }
}
