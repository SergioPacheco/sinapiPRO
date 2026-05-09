package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Departamento;
import com.sinapipro.repository.filter.DepartamentoFilter;
import com.sinapipro.repository.helper.departamento.DepartamentosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface DepartamentosRepository extends JpaRepository<Departamento, Long>, DepartamentosRepositoryQueries,
		NamedEntityRepository<Departamento>, FilterableRepository<Departamento, DepartamentoFilter> {

	Optional<Departamento> findByNomeIgnoreCase(String nome);
}
