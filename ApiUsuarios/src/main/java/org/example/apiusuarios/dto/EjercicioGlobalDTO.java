package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.CategoriaEjercicio;
import org.example.apiusuarios.model.Equipamiento;
import org.example.apiusuarios.model.EjercicioGlobal;

@Data
@NoArgsConstructor
public class EjercicioGlobalDTO {

    private Integer ejercicioGlobalId;
    private String nombre;
    private CategoriaEjercicio categoria;
    private String musculoPrimario;
    private String musculosSecundarios;
    private Equipamiento equipamiento;
    private String cueCoaching;

    public EjercicioGlobalDTO(EjercicioGlobal e) {
        this.ejercicioGlobalId = e.getEjercicioGlobalId();
        this.nombre = e.getNombre();
        this.categoria = e.getCategoria();
        this.musculoPrimario = e.getMusculoPrimario();
        this.musculosSecundarios = e.getMusculosSecundarios();
        this.equipamiento = e.getEquipamiento();
        this.cueCoaching = e.getCueCoaching();
    }
}
