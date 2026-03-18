package br.edu.ifrn.sinapiPRO.repository.helper.composicaogrupo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.ComposicaoGrupo;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoGrupoFilter;

public interface ComposicaoGruposRepositoryQueries {

	public Page<ComposicaoGrupo> filtrar(ComposicaoGrupoFilter filtro, Pageable pageable);
	
}