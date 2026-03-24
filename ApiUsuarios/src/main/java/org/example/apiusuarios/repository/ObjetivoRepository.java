package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.Objetivo;
import org.example.apiusuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObjetivoRepository extends JpaRepository<Objetivo, Integer> {

    List<Objetivo> findByUsuarioOrderByFechaInicioDesc(Usuario usuario);

    Optional<Objetivo> findFirstByUsuarioOrderByFechaInicioDesc(Usuario usuario);

    List<Objetivo> findByUsuarioAndFechaInicioBetween(Usuario usuario, LocalDate from, LocalDate to);

    boolean existsByUsuarioAndFechaInicio(Usuario usuario, LocalDate fechaInicio);

    Optional<Objetivo> findFirstByUsuarioAndFechaInicioOrderByObjetivoIdDesc(Usuario usuario, LocalDate fechaInicio);

}
