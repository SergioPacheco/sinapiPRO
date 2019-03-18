package br.edu.ifrn.sinapiPRO.repository.helper.item;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.repository.filter.AtualFilter;

public interface ItemRepositoryQueries {
 
	public Page<Item> filtrar(AtualFilter filtro, Pageable pageable);
	
	public List<Etapa> findEtapasOrcamento(Long codigo); 
	
	//public Item buscarOrcamentoComItens(Long codigo);
	//public BigDecimal valorTotalNoAno();
	//public BigDecimal valorTotalNoMes();
	//public BigDecimal valorTicketMedioNoAno();
	 
	//public List<OrcamentoMes> totalPorMes();
	//public List<OrcamentoBase> totalPorBase();
	
}
