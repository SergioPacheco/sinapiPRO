package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.FormaPagamento;
import br.edu.ifrn.sinapiPRO.repository.filter.FormaPagamentoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.formapagamento.FormasPagamentoRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface FormasPagamentoRepository extends JpaRepository<FormaPagamento, Long>, FormasPagamentoRepositoryQueries,
		NamedEntityRepository<FormaPagamento>, FilterableRepository<FormaPagamento, FormaPagamentoFilter> {

	Optional<FormaPagamento> findByNomeIgnoreCase(String nome);
}
