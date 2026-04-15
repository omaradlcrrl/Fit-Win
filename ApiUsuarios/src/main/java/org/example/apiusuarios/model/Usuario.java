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

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comida> comidas;

    // Rutinas y Sesiones
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rutina> rutinas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SesionEntrenamiento> sesionesEntrenamiento;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ejercicio> ejercicios;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecordPersonal> recordsPersonales;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FotoProgreso> fotosProgreso;

    // Body Metrics y Objetivos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MedicionCorporal> medicionesCorporales;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Objetivo> historicoObjetivos;

    private String genero; // Added new field

    private String nivelActividad; // Added new field
    private Double pesoActual;
    private String objetivo;
    private Boolean onboardingCompleto = false;

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