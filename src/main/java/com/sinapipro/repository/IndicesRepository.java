package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Indice;
import com.sinapipro.repository.filter.IndiceFilter;
import com.sinapipro.repository.helper.indice.IndicesRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface IndicesRepository extends JpaRepository<Indice, Long>, IndicesRepositoryQueries,
		NamedEntityRepository<Indice>, FilterableRepository<Indice, IndiceFilter> {

	Optional<Indice> findByNomeIgnoreCase(String nome);
}
