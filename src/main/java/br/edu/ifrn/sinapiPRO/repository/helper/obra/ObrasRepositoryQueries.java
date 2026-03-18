package br.edu.ifrn.sinapiPRO.repository.helper.obra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.Obra;
import br.edu.ifrn.sinapiPRO.repository.filter.ObraFilter;

public interface ObrasRepositoryQueries {
	
	public Page<Obra> filtrar(ObraFilter filter, Pageable pageable);

	Obra buscarComCidadeEstado(Long codigo);
}
