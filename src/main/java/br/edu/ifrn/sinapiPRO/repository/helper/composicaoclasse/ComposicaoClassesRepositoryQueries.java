package br.edu.ifrn.sinapiPRO.repository.helper.composicaoclasse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoClasseFilter;

public interface ComposicaoClassesRepositoryQueries {
	
	public Page<ComposicaoClasse> filtrar(ComposicaoClasseFilter filtro, Pageable pageable);
	
}
