package br.edu.ifrn.sinapiPRO.repository.helper.composicao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.dto.ComposicaoDTO;
import br.edu.ifrn.sinapiPRO.model.Composicao;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoFilter;

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
