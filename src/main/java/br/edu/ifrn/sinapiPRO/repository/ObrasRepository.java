package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.model.Obra;
import br.edu.ifrn.sinapiPRO.repository.helper.obra.ObrasRepositoryQueries;

public interface ObrasRepository extends JpaRepository<Obra, Long>, ObrasRepositoryQueries {

	public List<Obra> findByNomeStartingWithIgnoreCase(String nome);

	public Optional<Obra> findByCei(String cei);

}