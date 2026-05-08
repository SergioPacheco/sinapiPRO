package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.model.Estado;
import br.edu.ifrn.sinapiPRO.repository.filter.EstadoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.estado.EstadosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

public interface EstadosRepository extends JpaRepository<Estado, Long>, EstadosRepositoryQueries,
		NamedEntityRepository<Estado>, FilterableRepository<Estado, EstadoFilter> {
	
	public Optional<Estado> findByNomeIgnoreCase(String nome);
	public Optional<Estado> findBySiglaIgnoreCase(String sigla);
	
}
