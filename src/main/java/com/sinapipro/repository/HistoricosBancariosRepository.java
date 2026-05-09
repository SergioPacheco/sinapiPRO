package com.sinapipro.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.HistoricoBancario;
@Repository
public interface HistoricosBancariosRepository extends JpaRepository<HistoricoBancario, Long> {}
