package org.example.apiusuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apiusuarios.model.Usuario;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UsuarioDTO {

    private Integer usuarioId;
    private String nombre;
    private String password;
    private String apellidos;
    private String correoElectronico;
    private LocalDate fechaNacimiento;
    private LocalDate fechaRegistro;
    private Double altura;
    private String idioma;
    private String estrategia;
    private Integer ajusteCalorico;
    private String genero;
    private String nivelActividad;

    public UsuarioDTO(Usuario u) {
        this.usuarioId = u.getUsuarioId();
        this.nombre = u.getNombre();
        this.password = u.getPassword();
        this.apellidos = u.getApellidos();
        this.correoElectronico = u.getCorreoElectronico();
        this.fechaNacimiento = u.getFechaNacimiento();
        this.fechaRegistro = u.getFechaRegistro();
        this.altura = u.getAltura();
        this.idioma = u.getIdioma();
        this.estrategia = u.getEstrategia();
        this.ajusteCalorico = u.getAjusteCalorico();
        this.genero = u.getGenero();
        this.nivelActividad = u.getNivelActividad();
    }
}
