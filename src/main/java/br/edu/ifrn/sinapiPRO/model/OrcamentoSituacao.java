package br.edu.ifrn.sinapiPRO.model;

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
