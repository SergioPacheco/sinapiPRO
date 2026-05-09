package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.DivisaoInsumo;
import com.sinapipro.repository.filter.DivisaoInsumoFilter;
import com.sinapipro.repository.helper.divisaoinsumo.DivisoesInsumoRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface DivisoesInsumoRepository extends JpaRepository<DivisaoInsumo, Long>, DivisoesInsumoRepositoryQueries,
		NamedEntityRepository<DivisaoInsumo>, FilterableRepository<DivisaoInsumo, DivisaoInsumoFilter> {

	Optional<DivisaoInsumo> findByNomeIgnoreCase(String nome);
}
