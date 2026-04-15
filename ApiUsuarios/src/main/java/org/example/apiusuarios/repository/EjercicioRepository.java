package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer> {
    List<Ejercicio> findByUsuario_UsuarioIdAndDiaSemanaOrderByPosicionAsc(Integer usuarioId, String diaSemana);
    List<Ejercicio> findByRutina_RutinaIdAndDiaSemanaOrderByPosicionAsc(Integer rutinaId, String diaSemana);
    List<Ejercicio> findByRutina_RutinaId(Integer rutinaId);
}
