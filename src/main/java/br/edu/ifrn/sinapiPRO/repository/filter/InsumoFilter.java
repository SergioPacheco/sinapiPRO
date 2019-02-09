package br.edu.ifrn.sinapiPRO.repository.filter;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Especie;

public class InsumoFilter {

	private BaseInsumo baseInsumo; 
	private Long codigoInsumo;
	private String descricao;
	private Especie especie;
	
	public BaseInsumo getBaseInsumo() {
		return baseInsumo;
	}
	public void setBaseInsumo(BaseInsumo baseInsumo) {
		this.baseInsumo = baseInsumo;
	}
	public Long getCodigoInsumo() {
		return codigoInsumo;
	}
	public void setCodigoInsumo(Long codigoInsumo) {
		this.codigoInsumo = codigoInsumo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Especie getEspecie() {
		return especie;
	}
	public void setEspecie(Especie especie) {
		this.especie = especie;
	}
}
