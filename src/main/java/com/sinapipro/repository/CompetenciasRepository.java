package com.sinapipro.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Competencia;

@Repository
public interface CompetenciasRepository extends JpaRepository<Competencia, Long> {
    Optional<Competencia> findByMesAndAno(Integer mes, Integer ano);
    List<Competencia> findByEncerradaFalseOrderByAnoDescMesDesc();
}
