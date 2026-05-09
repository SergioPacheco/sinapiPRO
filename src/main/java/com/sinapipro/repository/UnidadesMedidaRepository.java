package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.UnidadeMedida;
import com.sinapipro.repository.filter.UnidadeMedidaFilter;
import com.sinapipro.repository.helper.unidademedida.UnidadesMedidaRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface UnidadesMedidaRepository extends JpaRepository<UnidadeMedida, Long>, UnidadesMedidaRepositoryQueries,
		NamedEntityRepository<UnidadeMedida>, FilterableRepository<UnidadeMedida, UnidadeMedidaFilter> {

	Optional<UnidadeMedida> findByNomeIgnoreCase(String nome);
}
