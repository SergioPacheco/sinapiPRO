package br.edu.ifrn.sinapiPRO.repository.helper.especieinsumo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.EspecieInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.EspecieInsumoFilter;

public interface EspecieInsumosRepositoryQueries {

	Page<EspecieInsumo> filtrar(EspecieInsumoFilter filtro, Pageable pageable);
}
