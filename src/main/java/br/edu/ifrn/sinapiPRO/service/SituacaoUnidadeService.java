package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.SituacaoUnidade;
import br.edu.ifrn.sinapiPRO.repository.SituacoesUnidadeRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class SituacaoUnidadeService extends AbstractSimpleCrudService<SituacaoUnidade, SituacoesUnidadeRepository> {

	public SituacaoUnidadeService(SituacoesUnidadeRepository repository) {
		super(repository, "Impossível apagar. Já está em uso.", "Situação de unidade não encontrada.");
	}
}
