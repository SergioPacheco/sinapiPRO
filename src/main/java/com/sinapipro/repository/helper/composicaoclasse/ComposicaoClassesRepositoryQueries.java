package com.sinapipro.repository.helper.composicaoclasse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.ComposicaoClasse;
import com.sinapipro.repository.filter.ComposicaoClasseFilter;

public interface ComposicaoClassesRepositoryQueries {
	
	public Page<ComposicaoClasse> filtrar(ComposicaoClasseFilter filtro, Pageable pageable);
	
}
