package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Fornecedor;
import br.edu.ifrn.sinapiPRO.repository.filter.FornecedorFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.fornecedor.FornecedoresRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface FornecedoresRepository extends JpaRepository<Fornecedor, Long>, FornecedoresRepositoryQueries,
		NamedEntityRepository<Fornecedor>, FilterableRepository<Fornecedor, FornecedorFilter> {

	Optional<Fornecedor> findByNomeIgnoreCase(String nome);
}
