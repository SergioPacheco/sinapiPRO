package com.sinapipro.repository.helper.estado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.Estado;
import com.sinapipro.repository.filter.EstadoFilter;

public interface EstadosRepositoryQueries {
	
	public Page<Estado> filtrar(EstadoFilter filtro, Pageable pageable);
	
}
