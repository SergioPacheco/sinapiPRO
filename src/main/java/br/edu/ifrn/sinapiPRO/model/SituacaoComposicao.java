package br.edu.ifrn.sinapiPRO.model;

public enum  SituacaoComposicao {

	ATIVA("Ativa"), 
	CANCELADA("Cancelada");

	private String descricao;

	SituacaoComposicao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
