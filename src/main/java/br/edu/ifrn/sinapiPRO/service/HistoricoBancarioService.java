package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.HistoricoBancario;
import br.edu.ifrn.sinapiPRO.repository.HistoricosBancariosRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class HistoricoBancarioService extends AbstractSimpleCrudService<HistoricoBancario, HistoricosBancariosRepository> {

	public HistoricoBancarioService(HistoricosBancariosRepository repository) {
		super(repository, "Impossível apagar.", "Histórico bancário não encontrado.");
	}
}
