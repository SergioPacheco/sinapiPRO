package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.TipoCusto;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoCustoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.tipocusto.TipoCustosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface TipoCustosRepository extends JpaRepository<TipoCusto, Long>, TipoCustosRepositoryQueries,
		NamedEntityRepository<TipoCusto>, FilterableRepository<TipoCusto, TipoCustoFilter> {

	Optional<TipoCusto> findByNomeIgnoreCase(String nome);
}
