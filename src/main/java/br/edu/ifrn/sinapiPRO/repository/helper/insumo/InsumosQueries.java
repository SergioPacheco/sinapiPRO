package br.edu.ifrn.sinapiPRO.repository.helper.insumo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.dto.InsumoDTO;
import br.edu.ifrn.sinapiPRO.dto.ItemBasePrecoDTO;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.filter.InsumoFilter;

public interface InsumosQueries {

	public Page<Insumo> filtrar(InsumoFilter filtro, Pageable pageable);
	
	public List<InsumoDTO> porCodigoInsumoOuDescricao(String skuOuDescricao);
	
	public List<ItemBasePrecoDTO> listaPrecosPorInsumo(Long codigoInsumo);
	
}
