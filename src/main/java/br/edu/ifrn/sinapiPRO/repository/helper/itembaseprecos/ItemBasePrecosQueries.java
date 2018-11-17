package br.edu.ifrn.sinapiPRO.repository.helper.itembaseprecos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.ItemBasePreco;
import br.edu.ifrn.sinapiPRO.repository.filter.ItemBasePrecoFilter;

public interface ItemBasePrecosQueries {
	
	public Page<ItemBasePreco> filtrar(ItemBasePrecoFilter filtro, Pageable pageable);

	ItemBasePreco buscarComBasePreco(Long codigoItem);
	
}
