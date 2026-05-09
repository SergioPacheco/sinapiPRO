package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.TipoObra;
import com.sinapipro.repository.filter.TipoObraFilter;
import com.sinapipro.repository.helper.tipoobra.TiposObraRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface TiposObraRepository extends JpaRepository<TipoObra, Long>, TiposObraRepositoryQueries,
		NamedEntityRepository<TipoObra>, FilterableRepository<TipoObra, TipoObraFilter> {

	Optional<TipoObra> findByNomeIgnoreCase(String nome);
}
