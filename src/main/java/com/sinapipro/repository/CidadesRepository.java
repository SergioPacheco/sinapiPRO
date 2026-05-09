package com.sinapipro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.model.Cidade;
import com.sinapipro.model.Estado;
import com.sinapipro.repository.filter.CidadeFilter;
import com.sinapipro.repository.helper.cidade.CidadesRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;

public interface CidadesRepository extends JpaRepository<Cidade, Long>, CidadesRepositoryQueries,
		FilterableRepository<Cidade, CidadeFilter> {

	public List<Cidade> findByEstadoCodigo(Long codigoEstado);

	public Optional<Cidade> findByNomeAndEstado(String nome, Estado estado);
	
}
