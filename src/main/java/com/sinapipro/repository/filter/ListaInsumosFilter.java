package com.sinapipro.repository.filter;

import com.sinapipro.model.Especie;
import com.sinapipro.model.Orcamento;

public class ListaInsumosFilter {

	private Orcamento orcamento;
	private String descricao;
	private Especie especie;
	
	public Orcamento getOrcamento() {
		return orcamento;
	}
	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Especie getEspecie() {
		return especie;
	}
	public void setEspecie(Especie especie) {
		this.especie = especie;
	}
}
