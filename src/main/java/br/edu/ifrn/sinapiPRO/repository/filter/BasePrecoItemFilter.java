package br.edu.ifrn.sinapiPRO.repository.filter;

import br.edu.ifrn.sinapiPRO.model.BasePreco;

public class BasePrecoItemFilter {
	
	private BasePreco basePreco; 
	private String codigoInsumo;
	
	public BasePreco getBasePreco() {
		return basePreco;
	}
	public void setBasePreco(BasePreco basePreco) {
		this.basePreco = basePreco;
	}
	public String getCodigoInsumo() {
		return codigoInsumo;
	}
	public void setCodigoInsumo(String codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}

}
