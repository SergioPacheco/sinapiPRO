package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.TipoCusto;
import br.edu.ifrn.sinapiPRO.repository.helper.tipocusto.TipoCustosRepositoryQueries;

@Repository
public interface TipoCustosRepository extends JpaRepository<TipoCusto, Long>, TipoCustosRepositoryQueries {

	Optional<TipoCusto> findByNomeIgnoreCase(String nome);
}
