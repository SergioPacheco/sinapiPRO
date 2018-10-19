package br.edu.ifrn.sinapiPRO.repository.helper.baseinsumos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.BaseInsumoFilter;

public interface BaseInsumosQueries {
	
	public Page<BaseInsumo> filtrar(BaseInsumoFilter filtro, Pageable pageable);
	
}
