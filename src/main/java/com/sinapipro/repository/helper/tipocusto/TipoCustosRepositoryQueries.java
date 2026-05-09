package com.sinapipro.repository.helper.tipocusto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.TipoCusto;
import com.sinapipro.repository.filter.TipoCustoFilter;

public interface TipoCustosRepositoryQueries {

	Page<TipoCusto> filtrar(TipoCustoFilter filtro, Pageable pageable);
}
