package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Cotacao;
import com.sinapipro.repository.CotacoesRepository;
import com.sinapipro.service.support.AbstractObraScopedCrudService;

@Service
public class CotacaoService extends AbstractObraScopedCrudService<Cotacao, CotacoesRepository> {

	private final CotacoesRepository repository;

	public CotacaoService(CotacoesRepository repository) {
		super(repository, "Impossível apagar a cotação.", "Cotação não encontrada.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public Cotacao salvar(Cotacao cotacao) {
		cotacao.getItens().forEach(item -> item.setCotacao(cotacao));
		cotacao.getFornecedores().forEach(fornecedor -> fornecedor.setCotacao(cotacao));
		return repository.saveAndFlush(cotacao);
	}

	@Transactional(readOnly = true)
	public Cotacao buscarComItens(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
