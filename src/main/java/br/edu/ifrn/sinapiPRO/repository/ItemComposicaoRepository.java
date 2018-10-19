package br.edu.ifrn.sinapiPRO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.ItemComposicao;

@Repository
public interface ItemComposicaoRepository extends JpaRepository<ItemComposicao, Long> {
	
	
}
