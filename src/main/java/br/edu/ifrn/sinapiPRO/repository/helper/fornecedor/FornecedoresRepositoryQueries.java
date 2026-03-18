package br.edu.ifrn.sinapiPRO.repository.helper.fornecedor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.Fornecedor;
import br.edu.ifrn.sinapiPRO.repository.filter.FornecedorFilter;

public interface FornecedoresRepositoryQueries {
	Page<Fornecedor> filtrar(FornecedorFilter filtro, Pageable pageable);
}
