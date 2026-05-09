package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.BasePreco;
import com.sinapipro.repository.filter.BasePrecoFilter;
import com.sinapipro.repository.helper.baseprecos.BasePrecosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface BasePrecosRepository extends JpaRepository<BasePreco, Long>, BasePrecosRepositoryQueries,
		NamedEntityRepository<BasePreco>, FilterableRepository<BasePreco, BasePrecoFilter> {

	public Optional<BasePreco> findByNomeIgnoreCase(String nome);
 
}
