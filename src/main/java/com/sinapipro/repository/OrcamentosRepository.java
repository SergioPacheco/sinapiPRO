package com.sinapipro.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Orcamento;
import com.sinapipro.model.TipoOrcamento;
import com.sinapipro.repository.helper.orcamento.OrcamentosRepositoryQueries;

@Repository
public interface OrcamentosRepository extends JpaRepository<Orcamento, Long>, OrcamentosRepositoryQueries {

	Optional<Orcamento> findByNomeIgnoreCase(String nome);
	Optional<Orcamento> findTopByOrderByCodigoDesc();
	List<Orcamento> findByTipoOrcamento(TipoOrcamento tipoOrcamento);
}
