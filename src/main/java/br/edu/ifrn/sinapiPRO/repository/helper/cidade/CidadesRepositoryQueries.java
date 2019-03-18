package br.edu.ifrn.sinapiPRO.repository.helper.cidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.Cidade;
import br.edu.ifrn.sinapiPRO.repository.filter.CidadeFilter;

public interface CidadesRepositoryQueries {

	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable);
	
	public Cidade buscarComEstado(Long codigo);
	
}
