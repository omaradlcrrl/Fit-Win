package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.Entrenamiento;
import org.example.apiusuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Integer> {

    int countByUsuarioAndDiaSemana(Usuario usuario, String diaSemana);

    List<Entrenamiento> findByUsuarioAndDiaSemanaOrderByPosicionEjercicio(Usuario usuario, String diaSemana);

    Optional<Entrenamiento> findByUsuarioAndDiaSemanaAndPosicionEjercicio(
            Usuario usuario, String diaSemana, Integer posicionEjercicio
    );
}
