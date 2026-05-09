package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Empresa;
import com.sinapipro.repository.filter.EmpresaFilter;
import com.sinapipro.repository.helper.empresa.EmpresasRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface EmpresasRepository extends JpaRepository<Empresa, Long>, EmpresasRepositoryQueries,
		NamedEntityRepository<Empresa>, FilterableRepository<Empresa, EmpresaFilter> {

	Optional<Empresa> findByNomeIgnoreCase(String nome);
}
