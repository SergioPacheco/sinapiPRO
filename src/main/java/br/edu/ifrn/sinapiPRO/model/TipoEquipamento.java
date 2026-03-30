package br.edu.ifrn.sinapiPRO.model;

public enum TipoEquipamento {
	INTERNO("Interno"),
	EXTERNO("Externo");

	private String descricao;
	TipoEquipamento(String descricao) { this.descricao = descricao;
}
	public String getDescricao() {
		return descricao;
	}
}
