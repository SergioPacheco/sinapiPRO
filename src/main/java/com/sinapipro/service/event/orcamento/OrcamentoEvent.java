package com.sinapipro.service.event.orcamento;

import com.sinapipro.model.Orcamento;

public class OrcamentoEvent {

	private Orcamento orcamento;

	public OrcamentoEvent(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	public Orcamento getOrcamento() {
		return orcamento;
	}
}
