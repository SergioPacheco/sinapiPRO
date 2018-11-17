package br.edu.ifrn.sinapiPRO.repository.helper.classe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.ClasseComposicao;
import br.edu.ifrn.sinapiPRO.repository.filter.ClasseComposicaoFilter;

public interface ClassesRepositoryQueries {
	
	public Page<ClasseComposicao> filtrar(ClasseComposicaoFilter filtro, Pageable pageable);
	
}
