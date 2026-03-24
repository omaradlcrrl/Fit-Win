package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "entrenamiento")
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entrenamiento_id")
    private Integer entrenamientoId;
    private String nombre;
    private String diaSemana;
    private Integer posicionEjercicio;
    private Integer series;
    private Integer repeticiones;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


}
