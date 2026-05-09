package com.sinapipro.repository.helper.obra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.Obra;
import com.sinapipro.repository.filter.ObraFilter;

public interface ObrasRepositoryQueries {
	
	public Page<Obra> filtrar(ObraFilter filter, Pageable pageable);

	Obra buscarComCidadeEstado(Long codigo);
}
