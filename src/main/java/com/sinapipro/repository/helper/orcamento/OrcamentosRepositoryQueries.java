package com.sinapipro.repository.helper.orcamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.Orcamento;
import com.sinapipro.repository.filter.OrcamentoFilter;

public interface OrcamentosRepositoryQueries {
	
	public Page<Orcamento> filtrar(OrcamentoFilter filtro, Pageable pageable);
	
	public Orcamento buscarComItens(Long codigo);
	
}
