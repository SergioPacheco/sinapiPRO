package com.sinapipro.repository.helper.composicao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.dto.ComposicaoDTO;
import com.sinapipro.model.Composicao;
import com.sinapipro.repository.filter.ComposicaoFilter;

public interface ComposicaoRepositoryQueries {

	public Page<Composicao> filtrar(ComposicaoFilter filtro, Pageable pageable);
	
	public Composicao buscarComItens(Long codigo);
	
	public  List<ComposicaoDTO> porDescricao(String descricao); 
	
	// public BigDecimal valorTotalNoAno();
	// public BigDecimal valorTotalNoMes();
	// public BigDecimal valorTicketMedioNoAno();
	
	// public List<ComposicaoMes> totalPorMes();
	// public List<ComposicaoOrigem> totalPorOrigem(); 
	
}
