package com.sinapipro.repository.helper.item;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.model.Etapa;
import com.sinapipro.model.Item;
import com.sinapipro.repository.filter.AtualFilter;

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
