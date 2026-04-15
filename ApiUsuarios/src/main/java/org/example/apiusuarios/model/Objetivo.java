package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "objetivo")
public class Objetivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "objetivo_id")
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

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void initFechas() {
        if (this.fechaInicio == null) this.fechaInicio = LocalDate.now();
        if (this.fechaFin == null) this.fechaFin = LocalDate.now().plusDays(1);
        if (this.activo == null) this.activo = Boolean.TRUE;
    }
}