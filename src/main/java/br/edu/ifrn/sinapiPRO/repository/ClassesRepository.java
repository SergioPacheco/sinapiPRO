package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.ClasseComposicao;
import br.edu.ifrn.sinapiPRO.repository.helper.classe.ClassesRepositoryQueries;

@Repository
public interface ClassesRepository extends JpaRepository<ClasseComposicao, Long>, ClassesRepositoryQueries {

	public Optional<ClasseComposicao> findByNomeIgnoreCase(String nome);
	public Optional<ClasseComposicao> findBySiglaIgnoreCase(String sigla);
	
}
