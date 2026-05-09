package com.sinapipro.dto;

import com.sinapipro.model.BaseInsumo;
import com.sinapipro.model.Especie;

public class ListaInsumos {

	private BaseInsumo baseInsumo;
	private Especie especie; 
	private String ordem; 
	private String tipoOrdem;

	public BaseInsumo getBaseInsumo() {
		return baseInsumo;
	}
	
	public Especie getEspecie() {
		return especie;
	}

	public void setEspecie(Especie especie) {
		this.especie = especie;
	}

	public void setBaseInsumo(BaseInsumo baseInsumo) {
		this.baseInsumo = baseInsumo;
	}

	public String getOrdem() {
		return ordem;
	}

	public void setOrdem(String ordem) {
		this.ordem = ordem;
	}

	public String getTipoOrdem() {
		return tipoOrdem;
	}

	public void setTipoOrdem(String tipoOrdem) {
		this.tipoOrdem = tipoOrdem;
	}
	
}
