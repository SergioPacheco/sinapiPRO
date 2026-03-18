package br.edu.ifrn.sinapiPRO.repository.helper.tipocusto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.TipoCusto;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoCustoFilter;

public interface TipoCustosRepositoryQueries {

	Page<TipoCusto> filtrar(TipoCustoFilter filtro, Pageable pageable);
}
