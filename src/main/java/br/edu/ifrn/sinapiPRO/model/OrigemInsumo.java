package br.edu.ifrn.sinapiPRO.model;

public enum OrigemInsumo {
	PROPRIO("Próprio"),
	TERCEIRO("Terceiro");

	private String descricao;
	OrigemInsumo(String descricao) { this.descricao = descricao; }
	public String getDescricao() { return descricao; }
}
