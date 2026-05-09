package com.sinapipro.model;

public enum Especie {

	MATERIAL("Material"),
	EQUIPAMENTO("Equipamento"),
	MAO_DE_OBRA("Mão de Obra");
	
	private String descricao;
	
	Especie(String descricao) {
		this.descricao = descricao;
	}
	 
	public String getDescricao() {
		return descricao;
	}
	
}
