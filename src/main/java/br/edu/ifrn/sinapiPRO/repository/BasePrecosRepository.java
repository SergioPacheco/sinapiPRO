package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.repository.filter.BasePrecoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.baseprecos.BasePrecosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface BasePrecosRepository extends JpaRepository<BasePreco, Long>, BasePrecosRepositoryQueries,
		NamedEntityRepository<BasePreco>, FilterableRepository<BasePreco, BasePrecoFilter> {

	public Optional<BasePreco> findByNomeIgnoreCase(String nome);
 
}
