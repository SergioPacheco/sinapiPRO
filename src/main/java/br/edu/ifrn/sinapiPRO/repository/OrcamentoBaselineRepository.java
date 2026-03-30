package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.OrcamentoBaseline;

@Repository
public interface OrcamentoBaselineRepository extends JpaRepository<OrcamentoBaseline, Long> {

	List<OrcamentoBaseline> findByOrcamentoCodigoOrderByDataGravacaoDesc(Long codigoOrcamento);
}
