package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.TipoComposicao;

@Repository
public interface TipoComposicaoRepository extends JpaRepository<TipoComposicao, Long> {

	public Optional<TipoComposicao> findByNomeIgnoreCase(String nome);
	 
}
