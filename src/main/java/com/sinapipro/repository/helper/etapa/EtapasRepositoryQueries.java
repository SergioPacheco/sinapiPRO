package com.sinapipro.repository.helper.etapa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.Etapa;
import com.sinapipro.repository.filter.EtapaFilter;

public interface EtapasRepositoryQueries {
	
	public Page<Etapa> filtrar(EtapaFilter filtro, Pageable pageable);
	
}
