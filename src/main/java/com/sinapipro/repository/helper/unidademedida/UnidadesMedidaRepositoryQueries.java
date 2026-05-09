package com.sinapipro.repository.helper.unidademedida;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.UnidadeMedida;
import com.sinapipro.repository.filter.UnidadeMedidaFilter;

public interface UnidadesMedidaRepositoryQueries {
	Page<UnidadeMedida> filtrar(UnidadeMedidaFilter filtro, Pageable pageable);
}
