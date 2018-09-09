package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Classe;
import br.edu.ifrn.sinapiPRO.repository.helper.classe.ClassesQueries;

@Repository
public interface Classes extends JpaRepository<Classe, Long>, ClassesQueries {

	public Optional<Classe> findByNomeIgnoreCase(String nome);
	public Optional<Classe> findBySiglaIgnoreCase(String sigla);
	
}
