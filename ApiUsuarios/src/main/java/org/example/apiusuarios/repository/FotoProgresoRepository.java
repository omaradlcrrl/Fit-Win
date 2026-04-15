package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.FotoProgreso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FotoProgresoRepository extends JpaRepository<FotoProgreso, Integer> {
    List<FotoProgreso> findByUsuario_UsuarioIdOrderByFechaDesc(Integer usuarioId);
}
