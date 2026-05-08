package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoClasseFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.composicaoclasse.ComposicaoClassesRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;

@Repository
public interface ComposicaoClassesRepository extends JpaRepository<ComposicaoClasse, Long>, ComposicaoClassesRepositoryQueries,
		FilterableRepository<ComposicaoClasse, ComposicaoClasseFilter> {

	public Optional<ComposicaoClasse> findByNomeIgnoreCase(String nome);
	public Optional<ComposicaoClasse> findBySiglaIgnoreCase(String sigla);
	
}
