package com.sinapipro.repository.helper.tipounidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.TipoUnidade;
import com.sinapipro.repository.filter.TipoUnidadeFilter;

public interface TipoUnidadesRepositoryQueries {

	Page<TipoUnidade> filtrar(TipoUnidadeFilter filtro, Pageable pageable);
}
