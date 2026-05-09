package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.model.Estado;
import com.sinapipro.repository.filter.EstadoFilter;
import com.sinapipro.repository.helper.estado.EstadosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

public interface EstadosRepository extends JpaRepository<Estado, Long>, EstadosRepositoryQueries,
		NamedEntityRepository<Estado>, FilterableRepository<Estado, EstadoFilter> {
	
	public Optional<Estado> findByNomeIgnoreCase(String nome);
	public Optional<Estado> findBySiglaIgnoreCase(String sigla);
	
}
