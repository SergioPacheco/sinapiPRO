package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.SituacaoUnidade;
import com.sinapipro.repository.SituacoesUnidadeRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class SituacaoUnidadeService extends AbstractSimpleCrudService<SituacaoUnidade, SituacoesUnidadeRepository> {

	public SituacaoUnidadeService(SituacoesUnidadeRepository repository) {
		super(repository, "Impossível apagar. Já está em uso.", "Situação de unidade não encontrada.");
	}
}
