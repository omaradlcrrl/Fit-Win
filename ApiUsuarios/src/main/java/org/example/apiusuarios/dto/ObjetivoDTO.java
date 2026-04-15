package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.Objetivo;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ObjetivoDTO {

    private Integer objetivoId;
    private String tipo;
    private Double caloriasObjetivo;
    private Double proteinasObjetivo;
    private Double carbohidratosObjetivo;
    private Double grasasObjetivo;
    private Double imc;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
    private Integer usuarioId;
    private Integer registroId;
    private Double peso;
    private Double altura;

    public ObjetivoDTO(Objetivo o) {
        this.objetivoId = o.getObjetivoId();
        this.tipo = o.getTipo();
        this.caloriasObjetivo = o.getCaloriasObjetivo();
        this.proteinasObjetivo = o.getProteinasObjetivo();
        this.carbohidratosObjetivo = o.getCarbohidratosObjetivo();
        this.grasasObjetivo = o.getGrasasObjetivo();
        this.imc = o.getImc();
        this.fechaInicio = o.getFechaInicio();
        this.fechaFin = o.getFechaFin();
        this.activo = o.getActivo();
        this.usuarioId = (o.getUsuario() != null) ? o.getUsuario().getUsuarioId() : null;
        this.registroId = null;
        this.altura = (o.getUsuario() != null) ? o.getUsuario().getAltura() : 0.0;
        this.peso = (o.getUsuario() != null) ? o.getUsuario().getPesoActual() : 0.0;
    }
}
