package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.TipoCusto;
import com.sinapipro.repository.filter.TipoCustoFilter;
import com.sinapipro.repository.helper.tipocusto.TipoCustosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface TipoCustosRepository extends JpaRepository<TipoCusto, Long>, TipoCustosRepositoryQueries,
		NamedEntityRepository<TipoCusto>, FilterableRepository<TipoCusto, TipoCustoFilter> {

	Optional<TipoCusto> findByNomeIgnoreCase(String nome);
}
