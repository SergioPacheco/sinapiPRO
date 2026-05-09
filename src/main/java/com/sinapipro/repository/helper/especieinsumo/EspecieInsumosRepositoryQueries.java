package com.sinapipro.repository.helper.especieinsumo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.EspecieInsumo;
import com.sinapipro.repository.filter.EspecieInsumoFilter;

public interface EspecieInsumosRepositoryQueries {

	Page<EspecieInsumo> filtrar(EspecieInsumoFilter filtro, Pageable pageable);
}
