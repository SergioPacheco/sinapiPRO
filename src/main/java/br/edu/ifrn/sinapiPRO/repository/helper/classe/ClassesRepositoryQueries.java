package br.edu.ifrn.sinapiPRO.repository.helper.classe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.repository.filter.ClasseComposicaoFilter;

public interface ClassesRepositoryQueries {
	
	public Page<ComposicaoClasse> filtrar(ClasseComposicaoFilter filtro, Pageable pageable);
	
}
