package com.sinapipro.model;

public enum OrcamentoSituacao {

	BLOQUEADO("Bloqueado"), 
	CONCLUIDO("Concluido"), 
	CANCELADO("Cancelado"),
	ABERTO("Aberto");

	private String descricao;

	OrcamentoSituacao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
