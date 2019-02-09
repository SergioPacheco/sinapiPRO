package br.edu.ifrn.sinapiPRO.repository.helper.baseprecositem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.BasePrecoItem;
import br.edu.ifrn.sinapiPRO.repository.filter.BasePrecoItemFilter;

public interface BasePrecosItemRepositoryQueries {
	
	public Page<BasePrecoItem> filtrar(BasePrecoItemFilter filtro, Pageable pageable);

	BasePrecoItem buscarComBasePreco(Long codigoItem);
	
}
