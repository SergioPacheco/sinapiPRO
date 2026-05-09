package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Tributo;
import com.sinapipro.repository.filter.TributoFilter;
import com.sinapipro.repository.helper.tributo.TributosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;

@Repository
public interface TributosRepository extends JpaRepository<Tributo, Long>, TributosRepositoryQueries,
		FilterableRepository<Tributo, TributoFilter> {

	Optional<Tributo> findByDescricaoIgnoreCase(String descricao);
}
