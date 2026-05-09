package com.sinapipro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.ComposicaoClasse;
import com.sinapipro.model.ComposicaoGrupo;
import com.sinapipro.repository.filter.ComposicaoGrupoFilter;
import com.sinapipro.repository.helper.composicaogrupo.ComposicaoGruposRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;

@Repository
public interface ComposicaoGruposRepository extends JpaRepository<ComposicaoGrupo, Long>, ComposicaoGruposRepositoryQueries,
		FilterableRepository<ComposicaoGrupo, ComposicaoGrupoFilter> {

	public Optional<ComposicaoGrupo> findByNomeIgnoreCase(String nome);
	
	public Optional<ComposicaoGrupo> findByNomeAndComposicaoClasse(String nome, ComposicaoClasse composicaoClasse);
	 
	public List<ComposicaoGrupo> findAllByComposicaoClasseCodigo(Long codigoComposicaoClasse);
	
	 
}
