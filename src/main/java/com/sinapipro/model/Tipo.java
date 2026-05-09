package com.sinapipro.model;

public enum Tipo {

	ETAPA("Etapa"),
	INSUMO("Insumo"),
	COMPOSICAO("Composição");
	
	private String descricao;
	
	Tipo(String descricao) {
		this.descricao = descricao;
	}
	 
	public String getDescricao() {
		return descricao;
	}
	
}
