package br.edu.ifrn.sinapiPRO.repository.helper.tributo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.Tributo;
import br.edu.ifrn.sinapiPRO.repository.filter.TributoFilter;

public interface TributosRepositoryQueries {

	Page<Tributo> filtrar(TributoFilter filtro, Pageable pageable);
}
