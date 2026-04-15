package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comida")
public class Comida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comida_id")
    private Integer comidaId;
    private String nombre;
    private Integer calorias;
    private Double carbohidratos;
    private Double grasasSaturadas;
    private Double proteinas;

    @Enumerated(EnumType.STRING)
    private TipoComida tipoComida;

    private Double cantidad;

    @Enumerated(EnumType.STRING)
    private UnidadComida unidad;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;




    @PrePersist
    public void definirFecha() {
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
    }
}