package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RutinaRepository extends JpaRepository<Rutina, Integer> {
    List<Rutina> findByUsuario_UsuarioId(Integer usuarioId);
}
