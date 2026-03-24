package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.Comida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Integer> {

    List<Comida> findByUsuario_UsuarioIdAndFecha(Integer usuarioid, LocalDate fecha);
}
