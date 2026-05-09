package com.sinapipro.repository.filter;

import com.sinapipro.model.Etapa;
import com.sinapipro.model.Orcamento;

public class AtualFilter {

	private Etapa etapa;
	private String descricaoItem;
	private Orcamento orcamento;

	public Etapa getEtapa() {
		return etapa;
	}

	public void setEtapa(Etapa etapa) {
		this.etapa = etapa;
	}

	public String getDescricaoItem() {
		return descricaoItem;
	}

	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	
	
	
}