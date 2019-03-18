package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.model.Estado;
import br.edu.ifrn.sinapiPRO.repository.helper.estado.EstadosRepositoryQueries;

public interface EstadosRepository extends JpaRepository<Estado, Long>, EstadosRepositoryQueries {
	
	public Optional<Estado> findByNomeIgnoreCase(String nome);
	public Optional<Estado> findBySiglaIgnoreCase(String sigla);
	
}
