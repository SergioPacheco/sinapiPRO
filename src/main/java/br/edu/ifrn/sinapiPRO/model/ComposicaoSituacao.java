package br.edu.ifrn.sinapiPRO.model;

public enum  ComposicaoSituacao {

	ATIVA("Ativa"), 
	CANCELADA("Cancelada");

	private String descricao;

	ComposicaoSituacao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
