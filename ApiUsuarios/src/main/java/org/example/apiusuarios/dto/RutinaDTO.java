package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.Rutina;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RutinaDTO {

    private Integer rutinaId;
    private String nombre;
    private String etiqueta;
    private String diasActivos;
    private Integer duracionEstimadaMin;
    private LocalDate fechaCreacion;
    private Integer usuarioId;

    public RutinaDTO(Rutina r) {
        this.rutinaId = r.getRutinaId();
        this.nombre = r.getNombre();
        this.etiqueta = r.getEtiqueta();
        this.diasActivos = r.getDiasActivos();
        this.duracionEstimadaMin = r.getDuracionEstimadaMin();
        this.fechaCreacion = r.getFechaCreacion();
        this.usuarioId = r.getUsuario() != null ? r.getUsuario().getUsuarioId() : null;
    }
}
