package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.HistoricoBancario;
import com.sinapipro.repository.HistoricosBancariosRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class HistoricoBancarioService extends AbstractSimpleCrudService<HistoricoBancario, HistoricosBancariosRepository> {

	public HistoricoBancarioService(HistoricosBancariosRepository repository) {
		super(repository, "Impossível apagar.", "Histórico bancário não encontrado.");
	}
}
