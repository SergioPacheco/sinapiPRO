package br.edu.ifrn.sinapiPRO.repository.filter;

import br.edu.ifrn.sinapiPRO.model.BasePreco;

public class BasePrecoItemFilter {
	
	private BasePreco basePreco; 
	private Long codigoInsumo;
	
	public BasePreco getBasePreco() {
		return basePreco;
	}
	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}
	public Long getCodigoInsumo() {
		return codigoInsumo;
	}
	public void setCodigoInsumo(Long codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}

}
