package com.sinapipro.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Etapa;
import com.sinapipro.repository.filter.EtapaFilter;
import com.sinapipro.repository.helper.etapa.EtapasRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface EtapasRepository extends JpaRepository<Etapa, Long>, EtapasRepositoryQueries,
		NamedEntityRepository<Etapa>, FilterableRepository<Etapa, EtapaFilter> {

	public Optional<Etapa> findByNomeIgnoreCase(String nome);
	
	public List<Etapa> findByNomeStartingWithIgnoreCase(String nome);
	
}
