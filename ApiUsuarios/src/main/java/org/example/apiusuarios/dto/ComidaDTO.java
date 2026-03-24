package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.Comida;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ComidaDTO {

    private Integer comidaId;
    private String nombre;
    private Integer calorias;
    private Double grasasSaturadas;
    private Double proteinas;
    private Double carbohidratos;
    private LocalDate fecha;
    private Integer usuarioId;

    public ComidaDTO(Comida c) {
        this.comidaId = c.getComidaId();
        this.nombre = c.getNombre();
        this.calorias = c.getCalorias();
        this.grasasSaturadas = c.getGrasasSaturadas();
        this.proteinas = c.getProteinas();
        this.carbohidratos = c.getCarbohidratos();
        this.fecha = c.getFecha();
        this.usuarioId = c.getUsuario() != null ? c.getUsuario().getUsuarioId() : null;
    }
}
