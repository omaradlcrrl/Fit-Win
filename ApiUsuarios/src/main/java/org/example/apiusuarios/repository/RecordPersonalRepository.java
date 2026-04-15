package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.RecordPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecordPersonalRepository extends JpaRepository<RecordPersonal, Integer> {
    List<RecordPersonal> findByUsuario_UsuarioIdOrderByFechaDesc(Integer usuarioId);
    List<RecordPersonal> findByUsuario_UsuarioIdAndEjercicioGlobal_EjercicioGlobalId(Integer usuarioId, Integer ejercicioGlobalId);
}
