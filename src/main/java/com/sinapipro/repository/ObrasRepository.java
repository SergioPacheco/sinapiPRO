package com.sinapipro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.model.Obra;
import com.sinapipro.repository.filter.ObraFilter;
import com.sinapipro.repository.helper.obra.ObrasRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;

public interface ObrasRepository extends JpaRepository<Obra, Long>, ObrasRepositoryQueries,
		FilterableRepository<Obra, ObraFilter> {

	public List<Obra> findByNomeStartingWithIgnoreCase(String nome);

	public Optional<Obra> findByCei(String cei);
}
