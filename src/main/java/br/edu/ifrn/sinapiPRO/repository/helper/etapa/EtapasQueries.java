package br.edu.ifrn.sinapiPRO.repository.helper.etapa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.repository.filter.EtapaFilter;

public interface EtapasQueries {
	
	public Page<Etapa> filtrar(EtapaFilter filtro, Pageable pageable);
	
}
