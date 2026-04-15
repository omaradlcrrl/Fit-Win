package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "serie_realizada")
public class SerieRealizada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serie_id")
    private Integer serieId;

    private Double pesoKg;
    private Integer repeticionesRealizadas;
    private Boolean completado;
    private Integer orden;

    @ManyToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionEntrenamiento sesion;

    @ManyToOne
    @JoinColumn(name = "ejercicio_id")
    private Ejercicio ejercicio;

    @PrePersist
    public void initCompletado() {
        if (this.completado == null) this.completado = false;
    }
}
