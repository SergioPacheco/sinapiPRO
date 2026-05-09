package com.sinapipro.repository.helper.baseinsumos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.BaseInsumo;
import com.sinapipro.repository.filter.BaseInsumoFilter;

public interface BaseInsumosRepositoryQueries {
	
	public Page<BaseInsumo> filtrar(BaseInsumoFilter filtro, Pageable pageable);
	
}
