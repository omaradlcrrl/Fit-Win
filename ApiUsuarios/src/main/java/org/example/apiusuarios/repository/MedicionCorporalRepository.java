package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.MedicionCorporal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicionCorporalRepository extends JpaRepository<MedicionCorporal, Integer> {

    List<MedicionCorporal> findByUsuario_UsuarioIdOrderByFechaDesc(Integer usuarioId);

    boolean existsByUsuario_UsuarioIdAndFecha(Integer usuarioId, LocalDate fecha);

    List<MedicionCorporal> findByUsuario_UsuarioIdAndFechaBetweenOrderByFechaAsc(Integer usuarioId, LocalDate from, LocalDate to);

    Optional<MedicionCorporal> findByUsuario_UsuarioIdAndFecha(Integer usuarioId, LocalDate fecha);

    Optional<MedicionCorporal> findFirstByUsuario_UsuarioIdOrderByFechaDesc(Integer usuarioId);
}