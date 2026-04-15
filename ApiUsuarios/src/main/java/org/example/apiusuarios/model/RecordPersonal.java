package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "record_personal")
public class RecordPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    private Double pesoKg;
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "ejercicio_global_id", nullable = false)
    private EjercicioGlobal ejercicioGlobal;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void initFecha() {
        if (this.fecha == null) this.fecha = LocalDate.now();
    }
}
