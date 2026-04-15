package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "medicioncorporal")
public class MedicionCorporal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicion_id")
    private Integer medicionId;

    private LocalDate fecha;

    private Double peso;
    private Double porcentajeGrasa;
    private Double masaMagra;
    private Double pecho;
    private Double espalda;
    private Double hombro;
    private Double brazo;
    private Double muslo;
    private Double cintura;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void setFechaPorDefecto() {
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
    }
}
