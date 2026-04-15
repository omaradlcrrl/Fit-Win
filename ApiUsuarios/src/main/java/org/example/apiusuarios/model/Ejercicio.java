package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ejercicio")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ejercicio_id")
    private Integer ejercicioId;

    @ManyToOne
    @JoinColumn(name = "ejercicio_global_id")
    private EjercicioGlobal ejercicioGlobal;

    @ManyToOne
    @JoinColumn(name = "rutina_id")
    private Rutina rutina;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String diaSemana;
    private Integer series;
    private Integer repeticionesMin;
    private Integer repeticionesMax;
    private Integer descansoSegundos;
    private Double pesoKg;
    private Integer posicion;
}
