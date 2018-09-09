package br.edu.ifrn.sinapiPRO.repository.helper.classe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.Classe;
import br.edu.ifrn.sinapiPRO.repository.filter.ClasseFilter;

public interface ClassesQueries {
	
	public Page<Classe> filtrar(ClasseFilter filtro, Pageable pageable);
	
}
