package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.repository.helper.etapa.EtapasRepositoryQueries;

@Repository
public interface EtapasRepository extends JpaRepository<Etapa, Long>, EtapasRepositoryQueries {

	public Optional<Etapa> findByNomeIgnoreCase(String nome);
	
	public List<Etapa> findByNomeStartingWithIgnoreCase(String nome);
	
}
