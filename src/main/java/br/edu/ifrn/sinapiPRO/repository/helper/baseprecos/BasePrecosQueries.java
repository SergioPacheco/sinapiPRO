package br.edu.ifrn.sinapiPRO.repository.helper.baseprecos;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.repository.filter.BasePrecoFilter;

public interface BasePrecosQueries {
	
	public Page<BasePreco> filtrar(BasePrecoFilter filtro, Pageable pageable);
	

}
