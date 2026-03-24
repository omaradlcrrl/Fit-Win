package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.Entrenamiento;

@Data
@NoArgsConstructor
public class EntrenamientoDTO {

    private Integer entrenamientoId;
    private Integer posicionEjercicio;
    private String nombre;
    private String diaSemana;
    private Integer series;
    private Integer repeticiones;
    private Integer usuarioId;

    public EntrenamientoDTO(Entrenamiento e) {
        this.entrenamientoId = e.getEntrenamientoId();
        this.posicionEjercicio = e.getPosicionEjercicio();
        this.nombre = e.getNombre();
        this.diaSemana = e.getDiaSemana();
        this.series = e.getSeries();
        this.repeticiones = e.getRepeticiones();

        if (e.getUsuario() != null) {
            this.usuarioId = e.getUsuario().getUsuarioId();
        }
    }
}
