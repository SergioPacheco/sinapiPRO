package br.edu.ifrn.sinapiPRO.repository.helper.orcamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.repository.filter.OrcamentoFilter;

public interface OrcamentosRepositoryQueries {
	
	public Page<Orcamento> filtrar(OrcamentoFilter filtro, Pageable pageable);
	
}
