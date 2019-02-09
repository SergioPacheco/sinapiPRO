package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.repository.helper.baseprecos.BasePrecosRepositoryQueries;

@Repository
public interface BasePrecosRepository extends JpaRepository<BasePreco, Long>, BasePrecosRepositoryQueries {

	public Optional<BasePreco> findByNomeIgnoreCase(String nome);
 
}
