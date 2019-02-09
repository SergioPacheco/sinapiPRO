package br.edu.ifrn.sinapiPRO.repository.helper.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.OrcamentoItem;
import br.edu.ifrn.sinapiPRO.repository.filter.ItemFilter;

public interface ItemsRepositoryQueries {
 
	public Page<OrcamentoItem> filtrar(ItemFilter filtro, Pageable pageable);
	
	// public Item buscarOrcamentoComItens(Long codigo);
	
	//public BigDecimal valorTotalNoAno();
	//public BigDecimal valorTotalNoMes();
	//public BigDecimal valorTicketMedioNoAno();
	 
	//public List<OrcamentoMes> totalPorMes();
	//public List<OrcamentoBase> totalPorBase();
	
}
