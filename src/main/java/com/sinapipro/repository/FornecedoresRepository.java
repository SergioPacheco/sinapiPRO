package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Fornecedor;
import com.sinapipro.repository.filter.FornecedorFilter;
import com.sinapipro.repository.helper.fornecedor.FornecedoresRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface FornecedoresRepository extends JpaRepository<Fornecedor, Long>, FornecedoresRepositoryQueries,
		NamedEntityRepository<Fornecedor>, FilterableRepository<Fornecedor, FornecedorFilter> {

	Optional<Fornecedor> findByNomeIgnoreCase(String nome);
}
