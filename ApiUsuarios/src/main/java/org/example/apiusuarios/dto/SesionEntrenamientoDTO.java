package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.SesionEntrenamiento;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SesionEntrenamientoDTO {

    private Integer sesionId;
    private Integer rutinaId;
    private String nombreRutina;
    private Integer usuarioId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer duracionMinutos;
    private Integer nivelIntensidad;
    private Integer nivelRecuperacion;

    public SesionEntrenamientoDTO(SesionEntrenamiento s) {
        this.sesionId = s.getSesionId();
        this.rutinaId = s.getRutina() != null ? s.getRutina().getRutinaId() : null;
        this.nombreRutina = s.getRutina() != null ? s.getRutina().getNombre() : null;
        this.usuarioId = s.getUsuario() != null ? s.getUsuario().getUsuarioId() : null;
        this.fechaInicio = s.getFechaInicio();
        this.fechaFin = s.getFechaFin();
        this.duracionMinutos = s.getDuracionMinutos();
        this.nivelIntensidad = s.getNivelIntensidad();
        this.nivelRecuperacion = s.getNivelRecuperacion();
    }
}
