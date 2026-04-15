package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.SesionEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SesionEntrenamientoRepository extends JpaRepository<SesionEntrenamiento, Integer> {
    List<SesionEntrenamiento> findByUsuario_UsuarioIdOrderByFechaInicioDesc(Integer usuarioId);
    List<SesionEntrenamiento> findByRutina_RutinaId(Integer rutinaId);
}
