package com.sinapipro.model;

public enum TipoOrcamento {

	ESTIMATIVA("Estimativa"),
	VENDA("Venda"),
	EXECUCAO("Execução");

	private String descricao;

	TipoOrcamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
