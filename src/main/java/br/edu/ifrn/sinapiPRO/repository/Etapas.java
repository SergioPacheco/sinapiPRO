package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.repository.helper.etapa.EtapasQueries;

@Repository
public interface Etapas extends JpaRepository<Etapa, Long>, EtapasQueries {

	public Optional<Etapa> findByNomeIgnoreCase(String nome);
	
}
