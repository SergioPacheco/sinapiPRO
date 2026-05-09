package com.sinapipro.repository.helper.cidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.Cidade;
import com.sinapipro.repository.filter.CidadeFilter;

public interface CidadesRepositoryQueries {

	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable);
	
	public Cidade buscarComEstado(Long codigo);
	
}
