package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Funcao;
import com.sinapipro.repository.filter.FuncaoFilter;
import com.sinapipro.repository.helper.funcao.FuncoesRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface FuncoesRepository extends JpaRepository<Funcao, Long>, FuncoesRepositoryQueries,
		NamedEntityRepository<Funcao>, FilterableRepository<Funcao, FuncaoFilter> {

	Optional<Funcao> findByNomeIgnoreCase(String nome);
}
