package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.FormaPagamento;
import com.sinapipro.repository.filter.FormaPagamentoFilter;
import com.sinapipro.repository.helper.formapagamento.FormasPagamentoRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface FormasPagamentoRepository extends JpaRepository<FormaPagamento, Long>, FormasPagamentoRepositoryQueries,
		NamedEntityRepository<FormaPagamento>, FilterableRepository<FormaPagamento, FormaPagamentoFilter> {

	Optional<FormaPagamento> findByNomeIgnoreCase(String nome);
}
