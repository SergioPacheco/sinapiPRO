package com.sinapipro.repository.helper.insumo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sinapipro.dto.BasePrecoItemDTO;
import com.sinapipro.dto.InsumoDTO;
import com.sinapipro.model.Insumo;
import com.sinapipro.model.Item;
import com.sinapipro.repository.filter.InsumoFilter;
import com.sinapipro.repository.filter.ListaInsumosFilter;

public interface InsumosRepositoryQueries {

	public Page<Insumo> filtrar(InsumoFilter filtro, Pageable pageable);
	
	public Page<Item> filtrarInsumos(ListaInsumosFilter filtro, Pageable pageable);
	
	public List<InsumoDTO> porDescricao(String descricao);
	
	public List<BasePrecoItemDTO> listaPrecosPorInsumo(String codigoInsumo);
	
}
