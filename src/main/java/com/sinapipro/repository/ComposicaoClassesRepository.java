package com.sinapipro.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.ComposicaoClasse;
import com.sinapipro.repository.filter.ComposicaoClasseFilter;
import com.sinapipro.repository.helper.composicaoclasse.ComposicaoClassesRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;

@Repository
public interface ComposicaoClassesRepository extends JpaRepository<ComposicaoClasse, Long>, ComposicaoClassesRepositoryQueries,
		FilterableRepository<ComposicaoClasse, ComposicaoClasseFilter> {

	public Optional<ComposicaoClasse> findByNomeIgnoreCase(String nome);
	public Optional<ComposicaoClasse> findBySiglaIgnoreCase(String sigla);
	
}
