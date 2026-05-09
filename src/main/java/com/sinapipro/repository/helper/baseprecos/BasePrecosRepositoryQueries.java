package com.sinapipro.repository.helper.baseprecos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.BasePreco;
import com.sinapipro.repository.filter.BasePrecoFilter;

public interface BasePrecosRepositoryQueries {
	
	public Page<BasePreco> filtrar(BasePrecoFilter filtro, Pageable pageable);
	
}
