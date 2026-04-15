package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.SerieRealizada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SerieRealizadaRepository extends JpaRepository<SerieRealizada, Integer> {
    List<SerieRealizada> findBySesion_SesionIdOrderByOrdenAsc(Integer sesionId);
}
