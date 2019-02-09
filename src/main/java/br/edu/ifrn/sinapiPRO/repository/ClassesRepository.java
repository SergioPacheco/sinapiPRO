package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.repository.helper.classe.ClassesRepositoryQueries;

@Repository
public interface ClassesRepository extends JpaRepository<ComposicaoClasse, Long>, ClassesRepositoryQueries {

	public Optional<ComposicaoClasse> findByNomeIgnoreCase(String nome);
	public Optional<ComposicaoClasse> findBySiglaIgnoreCase(String sigla);
	
}
