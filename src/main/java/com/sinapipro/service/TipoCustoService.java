package com.sinapipro.service;

import org.springframework.stereotype.Service;

import com.sinapipro.model.TipoCusto;
import com.sinapipro.repository.TipoCustosRepository;
import com.sinapipro.repository.filter.TipoCustoFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

@Service
public class TipoCustoService extends AbstractNamedEntityCrudService<TipoCusto, TipoCustoFilter, TipoCustosRepository> {

	public TipoCustoService(TipoCustosRepository repository) {
		super(
				repository,
				TipoCusto::getCodigo,
				TipoCusto::getNome,
				"Tipo de custo já cadastrado",
				"Impossível apagar tipo de custo. Já está em uso.",
				"Tipo de custo não encontrado.");
	}
}
