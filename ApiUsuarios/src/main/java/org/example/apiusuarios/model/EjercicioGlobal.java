package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ejercicio_global")
public class EjercicioGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ejercicio_global_id")
    private Integer ejercicioGlobalId;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private CategoriaEjercicio categoria;

    private String musculoPrimario;

    private String musculosSecundarios;

    @Enumerated(EnumType.STRING)
    private Equipamiento equipamiento;

    @Column(columnDefinition = "TEXT")
    private String cueCoaching;
}
