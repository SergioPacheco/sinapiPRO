package br.edu.ifrn.sinapiPRO.model;

public enum SituacaoOrcamento {

	BLOQUEADO("Bloqueado"), 
	EFETIVADO("Efetivado"), 
	CONCLUIDO("Concluido"), 
	CANCELADO("Cancelado"),
	ABERTO("Aberto");

	private String descricao;

	SituacaoOrcamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
