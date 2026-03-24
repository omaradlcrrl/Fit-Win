package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.RegistroDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface RegistroDiarioRepository extends JpaRepository<RegistroDiario, Integer> {
    Optional<RegistroDiario> findByUsuario_UsuarioIdAndFecha(Integer usuarioId, LocalDate fecha);
    Optional<RegistroDiario> findFirstByUsuario_UsuarioIdOrderByFechaDesc(Integer usuarioId);
    List<RegistroDiario> findByUsuario_UsuarioIdAndFechaBetweenOrderByFechaAsc(Integer usuarioId, LocalDate from, LocalDate to);
}
