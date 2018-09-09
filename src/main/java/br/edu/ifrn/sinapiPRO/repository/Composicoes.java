package br.edu.ifrn.sinapiPRO.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.repository.helper.composicao.ComposicoesQueries;

public interface Composicoes extends JpaRepository<Composicao, Long>, ComposicoesQueries {

	
	
	
}
