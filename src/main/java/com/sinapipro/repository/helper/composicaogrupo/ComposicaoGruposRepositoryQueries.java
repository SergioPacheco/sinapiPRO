package com.sinapipro.repository.helper.composicaogrupo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.ComposicaoGrupo;
import com.sinapipro.repository.filter.ComposicaoGrupoFilter;

public interface ComposicaoGruposRepositoryQueries {

	public Page<ComposicaoGrupo> filtrar(ComposicaoGrupoFilter filtro, Pageable pageable);
	
}