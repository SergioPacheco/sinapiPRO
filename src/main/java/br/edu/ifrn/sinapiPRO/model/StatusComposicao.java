package br.edu.ifrn.sinapiPRO.model;

public enum  StatusComposicao {

	ATIVA("Ativa"), 
	CANCELADA("Cancelada");

	private String descricao;

	StatusComposicao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
