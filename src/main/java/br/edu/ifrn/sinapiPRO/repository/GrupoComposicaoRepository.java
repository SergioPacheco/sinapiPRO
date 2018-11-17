package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.GrupoComposicao;

@Repository
public interface GrupoComposicaoRepository extends JpaRepository<GrupoComposicao, Long> {

	public Optional<GrupoComposicao> findByNomeIgnoreCase(String nome);
	 
}
