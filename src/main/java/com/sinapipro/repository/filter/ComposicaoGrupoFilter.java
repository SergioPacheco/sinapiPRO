package com.sinapipro.repository.filter;

import com.sinapipro.model.ComposicaoClasse;

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