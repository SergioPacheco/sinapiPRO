package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Atendimento;
import com.sinapipro.repository.AtendimentosRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class AtendimentoService extends AbstractSimpleCrudService<Atendimento, AtendimentosRepository> {

	private final AtendimentosRepository repository;

	public AtendimentoService(AtendimentosRepository repository) {
		super(repository, "Impossível apagar o atendimento.", "Atendimento não encontrado.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public Atendimento salvar(Atendimento atendimento) {
		atendimento.getOrdensServico().forEach(ordemServico -> ordemServico.setAtendimento(atendimento));
		return repository.saveAndFlush(atendimento);
	}

	@Transactional(readOnly = true)
	public List<Atendimento> findAbertos() {
		return repository.findBySituacaoOrderByDataAberturaDesc("ABERTO");
	}
}
