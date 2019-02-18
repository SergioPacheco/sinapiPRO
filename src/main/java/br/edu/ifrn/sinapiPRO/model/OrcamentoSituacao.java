package br.edu.ifrn.sinapiPRO.model;

public enum OrcamentoSituacao {

	BLOQUEADO("Bloqueado"), 
	EFETIVADO("Efetivado"), 
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
