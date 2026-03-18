package br.edu.ifrn.sinapiPRO.repository.filter;

import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;

public class ComposicaoGrupoFilter {

	private ComposicaoClasse composicaoClasse;
	private String nome;
	
	public ComposicaoClasse getComposicaoClasse() {
		return composicaoClasse;
	}
	public void setComposicaoClasse(ComposicaoClasse composicaoClasse) {
		this.composicaoClasse = composicaoClasse;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
}