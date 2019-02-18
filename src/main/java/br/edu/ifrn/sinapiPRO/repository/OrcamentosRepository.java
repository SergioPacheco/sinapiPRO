package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.repository.helper.orcamento.OrcamentosRepositoryQueries;

@Repository
public interface OrcamentosRepository extends JpaRepository<Orcamento, Long>, OrcamentosRepositoryQueries {

	public Optional<Orcamento> findByNomeIgnoreCase(String nome);
	
}
