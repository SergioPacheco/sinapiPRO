package com.sinapipro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.OrcamentoBaseline;

@Repository
public interface OrcamentoBaselineRepository extends JpaRepository<OrcamentoBaseline, Long> {

	List<OrcamentoBaseline> findByOrcamentoCodigoOrderByDataGravacaoDesc(Long codigoOrcamento);
}
