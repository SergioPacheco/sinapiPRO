package com.sinapipro.repository.helper.fornecedor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.Fornecedor;
import com.sinapipro.repository.filter.FornecedorFilter;

public interface FornecedoresRepositoryQueries {
	Page<Fornecedor> filtrar(FornecedorFilter filtro, Pageable pageable);
}
