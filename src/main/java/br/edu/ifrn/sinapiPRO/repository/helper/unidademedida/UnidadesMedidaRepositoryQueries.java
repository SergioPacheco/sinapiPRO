package br.edu.ifrn.sinapiPRO.repository.helper.unidademedida;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.UnidadeMedida;
import br.edu.ifrn.sinapiPRO.repository.filter.UnidadeMedidaFilter;

public interface UnidadesMedidaRepositoryQueries {
	Page<UnidadeMedida> filtrar(UnidadeMedidaFilter filtro, Pageable pageable);
}
