package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Requisicao;
import com.sinapipro.repository.RequisicoesRepository;
import com.sinapipro.service.support.AbstractObraScopedCrudService;

@Service
public class RequisicaoService extends AbstractObraScopedCrudService<Requisicao, RequisicoesRepository> {

	private final RequisicoesRepository repository;

	public RequisicaoService(RequisicoesRepository repository) {
		super(repository, "Impossível apagar a requisição.", "Requisição não encontrada.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public Requisicao salvar(Requisicao requisicao) {
		requisicao.getItens().forEach(item -> item.setRequisicao(requisicao));
		return repository.saveAndFlush(requisicao);
	}

	@Transactional(readOnly = true)
	public Requisicao buscarComItens(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
