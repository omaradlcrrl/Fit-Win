package org.example.apiusuarios.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer usuarioId;

    private String nombre;
    private String apellidos;

    @Column(name = "correoelectronico", unique = true)
    private String correoElectronico;

    private String password;
    private Double altura;
    private String idioma;
    private LocalDate fechaRegistro;
    private LocalDate fechaNacimiento;

    private String estrategia;

    private Integer ajusteCalorico;

    // Relaciones con CascadeType.ALL para borrar en cascada
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comida> comidas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RegistroDiario> registrosDiarios;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Entrenamiento> entrenamientos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MedicionCorporal> medicionesCorporales;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Objetivo> objetivos;

    private String genero; // Added new field

    private String nivelActividad; // Added new field

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    public void definirFecha() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
        if (this.estrategia == null)
            this.estrategia = "MANTENIMIENTO";
        if (this.ajusteCalorico == null)
            this.ajusteCalorico = 10;
        if (this.role == null) {
            this.role = Role.USER; // Por defecto todos son USER
        }

    }

    // Opcional: toString manual para evitar bucles (no incluir listas)
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + usuarioId +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correoElectronico + '\'' +
                '}';
    }
}