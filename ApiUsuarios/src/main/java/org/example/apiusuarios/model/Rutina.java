package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rutina")
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rutina_id")
    private Integer rutinaId;

    private String nombre;
    private String etiqueta;
    private String diasActivos;
    private Integer duracionEstimadaMin;
    private LocalDate fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ejercicio> ejercicios;

    @PrePersist
    public void initFecha() {
        if (this.fechaCreacion == null) this.fechaCreacion = LocalDate.now();
    }
}
