package br.edu.ifrn.sinapiPRO.repository.helper.tipounidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.TipoUnidade;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUnidadeFilter;

public interface TipoUnidadesRepositoryQueries {

	Page<TipoUnidade> filtrar(TipoUnidadeFilter filtro, Pageable pageable);
}
