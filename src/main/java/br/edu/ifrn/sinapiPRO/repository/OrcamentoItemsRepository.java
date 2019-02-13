package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.OrcamentoItem;
import br.edu.ifrn.sinapiPRO.repository.helper.orcamentoitem.OrcamentoItemsRepositoryQueries;

 
@Repository
public interface OrcamentoItemsRepository extends JpaRepository<OrcamentoItem, Long>, OrcamentoItemsRepositoryQueries {

	// public Optional<OrcamentoItem> findByEtapa(Etapa etapa);
	public Optional<OrcamentoItem> findByCodigoItem(Long codigoItem);
	//public Optional<OrcamentoItem> findByComposicao(Composicao etapa);
}