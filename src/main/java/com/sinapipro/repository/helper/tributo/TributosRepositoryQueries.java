package com.sinapipro.repository.helper.tributo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.Tributo;
import com.sinapipro.repository.filter.TributoFilter;

public interface TributosRepositoryQueries {

	Page<Tributo> filtrar(TributoFilter filtro, Pageable pageable);
}
