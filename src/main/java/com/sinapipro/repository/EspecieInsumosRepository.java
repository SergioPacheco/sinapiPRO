package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.EspecieInsumo;
import com.sinapipro.repository.filter.EspecieInsumoFilter;
import com.sinapipro.repository.helper.especieinsumo.EspecieInsumosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface EspecieInsumosRepository extends JpaRepository<EspecieInsumo, Long>, EspecieInsumosRepositoryQueries,
		NamedEntityRepository<EspecieInsumo>, FilterableRepository<EspecieInsumo, EspecieInsumoFilter> {

	Optional<EspecieInsumo> findByNomeIgnoreCase(String nome);
}
