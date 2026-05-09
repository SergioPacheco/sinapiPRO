package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.TipoUnidade;
import com.sinapipro.repository.filter.TipoUnidadeFilter;
import com.sinapipro.repository.helper.tipounidade.TipoUnidadesRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface TipoUnidadesRepository extends JpaRepository<TipoUnidade, Long>, TipoUnidadesRepositoryQueries,
		NamedEntityRepository<TipoUnidade>, FilterableRepository<TipoUnidade, TipoUnidadeFilter> {

	Optional<TipoUnidade> findByNomeIgnoreCase(String nome);
}
