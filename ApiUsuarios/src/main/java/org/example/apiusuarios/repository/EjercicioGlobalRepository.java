package org.example.apiusuarios.repository;

import org.example.apiusuarios.model.CategoriaEjercicio;
import org.example.apiusuarios.model.EjercicioGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EjercicioGlobalRepository extends JpaRepository<EjercicioGlobal, Integer> {
    List<EjercicioGlobal> findByCategoria(CategoriaEjercicio categoria);
    List<EjercicioGlobal> findByNombreContainingIgnoreCase(String nombre);
}
