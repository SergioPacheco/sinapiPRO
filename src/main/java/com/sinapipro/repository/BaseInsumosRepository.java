package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.BaseInsumo;
import com.sinapipro.repository.filter.BaseInsumoFilter;
import com.sinapipro.repository.helper.baseinsumos.BaseInsumosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface BaseInsumosRepository extends JpaRepository<BaseInsumo, Long>, BaseInsumosRepositoryQueries,
		NamedEntityRepository<BaseInsumo>, FilterableRepository<BaseInsumo, BaseInsumoFilter> {

	public Optional<BaseInsumo> findByNomeIgnoreCase(String nome);
}
