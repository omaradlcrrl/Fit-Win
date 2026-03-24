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
@Table(name = "registrodiario")
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registro_id")
    private Integer registroId;

    private LocalDate fecha;
    private Double peso;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToOne(mappedBy = "registroDiario", cascade = CascadeType.ALL)
    private Objetivo objetivoDiario;

    @PrePersist
    public void definirFecha() {
        if (this.fecha == null) {
            this.fecha = LocalDate.now();
        }
    }
}